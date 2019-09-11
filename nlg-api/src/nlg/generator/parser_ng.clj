(ns nlg.generator.parser-ng
  (:require [clojure.tools.logging :as log]
            [nlg.generator.ops :as ops]
            [clojure.string :as str]
            [nlg.api.lexicon :as lexicon]
            [nlg.api.dictionary :as dictionary-api]
            [nlg.generator.realizer :as realizer]
            [data-access.entities.dictionary :as dictionary-entity]
            [data-access.entities.amr :as amr-entity]
            [ccg-kit.grammar :as ccg]
            [ccg-kit.verbnet.ccg :as vn-ccg]))

(def parse-cnt (atom 0))
(defn reset-parse-cnt [] (reset! parse-cnt 0))

(defn node-plus-children [node children attrs ctx]
  (cons node children))

(declare parse-node)

(defn parse-segment
  [node attrs ctx]
  (let [children (map #(parse-node % attrs ctx) (node :children))]
    (log/debug "Children" children)
    children))

(defn parse-component
  [node attrs ctx]
  (let [main (parse-node (node :name) (assoc attrs :type :component) ctx)
        children (map #(parse-node % attrs ctx) (node :children))]
    (cons main children)))


(defn parse-product
  [node attrs ctx]
  (let [main (parse-node (node :name) (assoc attrs :type :product) ctx)
        children (flatten (map #(parse-node % attrs ctx) (node :children)))]
    (cons main children)))

(defn parse-relationship
  [node attrs ctx]
  (let [rel-name (node :relationshipType)
        main (ops/append-static (ops/get-word rel-name) ctx)
        children (map #(parse-node % (assoc attrs :type :benefit) ctx) (node :children))]
    (cons main children)))

(defn parse-lexicon
  ;; TODO: deprecate
  [node attrs ctx]
  (let [word (get node :text)
        response (-> {:query (str/lower-case word) :profile (ctx :reader-profile)} (lexicon/search nil) (get-in [:body :items]))
        synonyms (set (mapcat :synonyms response))]
    (map #(ops/append-dynamic % (merge attrs {:type :wordlist :class word}) ctx) synonyms)))

(defn parse-dictionary
  [node attrs ctx]
  (let [key (get node :name)
        reader-profile (ctx :reader-profile)
        words (dictionary-api/search (str/lower-case key) reader-profile)]
    (map #(ops/append-dynamic % (merge attrs {:type :wordlist :class key}) ctx) words)))

(defn parse-rhetorical [node attrs ctx]
  (let [children (map parse-node (node :children) attrs ctx)]
    (map ops/set-complement children)))

(defn parse-cell
  [node attrs ctx]
  (let [cell-name (keyword (node :name))
        idx (swap! parse-cnt inc)]
    (ops/append-dynamic {:cell cell-name :dyn-name (format "$%d" idx)} (assoc attrs :source :cell) ctx)))

(defn parse-quote
  [node attrs ctx]
  (let [idx (swap! parse-cnt inc)]
    (ops/append-dynamic {:quote (node :text) :dyn-name (format "$%d" idx) } (assoc attrs :source :quote) ctx)))
  
(defn parse-document-plan
  [node attrs ctx]
  (let [_ (reset-parse-cnt)
        statements (node :segments)
        children (map #(parse-node % attrs ctx) statements)]
    (log/debug "Parsed statements: " children)
    children))

(defn parse-if-statement
  [node attrs ctx]
  (log/tracef "Node: %s" node)
  (let [op (keyword (node :operator))
        t (keyword (node :type))
        cond-fn (case op
                  :== (partial =)
                  := (partial =))]
    (case t
      :Value-comparison (fn [data]
                          (let [v1 (realizer/get-value (-> (parse-node (node :value1) attrs ctx)
                                                           (:dynamic)
                                                           (first)) data)
                                v2 (realizer/get-value (-> (parse-node (node :value2) attrs ctx)
                                                           (:dynamic)
                                                           (first)) data)]
                            (log/tracef "Comparing: '%s' vs '%s'" v1 v2)
                            (cond-fn v1 v2))))))

(defn build-default-cond
  [node attrs ctx gate]
  (parse-node (node :thenExpression) (assoc attrs :gate gate) ctx))

(defn build-if-cond
  [node attrs ctx]
  (parse-node (node :thenExpression) (assoc attrs :gate (parse-if-statement (node :condition) attrs ctx)) ctx))

(defn parse-conditional
  [node attrs ctx]
  (let [conditions (node :conditions)
        usual-conds (filter #(= :If-condition (keyword (% :type))) conditions) ;; All of the If Statements
        group-negation (fn [data] (not-any? #(% data) (map #(parse-if-statement (% :condition) attrs ctx) usual-conds))) ;; All of the previous IFs must be false in order for else to be true
        default-conds (filter #(= :Default-condition (keyword (% :type))) conditions) ;; Else statement

        block (flatten (map #(build-if-cond % attrs ctx) usual-conds))]
    (if (empty? default-conds)
      block
      (flatten (conj block (build-default-cond (first default-conds) attrs ctx group-negation))))))

(defn parse-list
  [opts node attrs ctx]
  (let [children (node :children)
        results (case (opts :type)
                  :Any-of (parse-node (rand-nth children) attrs ctx))]
    results))

(defn parse-amr
  [node attrs ctx]
  (let [amr-attrs (assoc attrs :amr true)
        idx (swap! parse-cnt inc)
        vc (amr-entity/get-verbclass (node :conceptId))
        _ (log/debugf "Got VC: %s" vc)
        reader-profile (ctx :reader-profile)
        members (-> (node :dictionaryItem)
                    :itemId
                    (dictionary-api/search reader-profile))
        children (flatten
                  (map (fn [{:keys [name children]}]
                         (let [updated-attrs (assoc amr-attrs :title name)]
                           (map #(parse-node % updated-attrs ctx) (remove nil? children))))
                       (node :roles)))
        ;; AMR-Key is key used inside, eg. "Agent". Data-key is our linked CSV column, eg. ":actor"
        amr-key->data-key (into {}
                                (map (fn [{:keys [name attrs]}]
                                       (case (:source attrs)
                                         :cell [(str/upper-case (:title attrs)) (:cell name)]
                                         name))
                                     (flatten (map :dynamic children))))
        _ (log/debugf "AMR Children: %s" (pr-str children))
        replaces (map (fn [c]
                        (log/debugf "Parsing AMR children from: %s" c)
                        (let [title (:title (:attrs c))
                              dyn-name (get-in c [:name :dyn-name])]
                          {:original (format "{{%s}}" (str/upper-case title)) :replace dyn-name}))
                      (->> children
                           (map :dynamic)
                           (map first)))
        words (conj
               (map (fn [r] (:original r)) replaces)
               (first members))
        amr-grammars (vn-ccg/vn->grammar (assoc vc :members (map (fn [m] {:name m}) members)))
        amr-results (-> (map (fn [g] (apply (partial ccg/generate g) words)) amr-grammars)
                        (flatten))
        ;; TODO: should do some better mechanism for this in the future
        amr-restrictors (map (fn [f]
                               (let [restrict (filter #(contains? % :restrictors) (:syntax f))]
                                 (if (seq restrict)
                                   (fn [data]
                                     (every? (fn [[restrictors v]] ;; If for every part in rule ...
                                               (every? (fn [r] ;; ... every restrictor passes
                                                         (log/debugf "v: %s data: %s full-data: %s" v (get data v) data)
                                                         (case (:type r)
                                                           :count (case (:value r)
                                                                    :singular (not (str/includes? (get data v "") ","))
                                                                    :plural (str/includes? (get data v "") ","))
                                                           true)) ;; Ignore all other types for now
                                                       restrictors))
                                             (map
                                              (fn [pattern]
                                                [(:restrictors pattern) (get amr-key->data-key (str/upper-case (:value pattern)))])
                                              restrict)))
                                   (fn [_] true)))) ;; If no restrictors, just always return true)

                             (:frames vc)) ;; Hardcoded single case for now.
        _ (log/debugf "AMR Results: %s" (pr-str amr-results))
        _ (log/debugf "AMR Restrictors: %s" (pr-str amr-restrictors))]
    (when (seq? amr-results)
      (cons
       (ops/append-dynamic
        {:quotes (map (fn [[rule restrict]] {:value (ops/replace-multi rule replaces)
                                             :gate restrict})
                      (ops/zip amr-results amr-restrictors)) ;; All of the AMR variations are saved as array of quotes
         :dyn-name (format "$%d" idx) }
        (assoc attrs :source :quotes :type :amr)
        ctx)
       children))))

(defn parse-themrole
  [node attrs ctx]
  (let [title (:title node)]
    (map #(parse-node % attrs ctx) (:children node))))

(defn parse-unknown
  [node]
  (log/debugf "Unknown node: %s" node))

(defn parse-node
  [node attrs ctx]
  (let [t (keyword (node :type))]
    (case t
      :Document-plan (parse-document-plan node attrs ctx)
      :Segment (parse-segment node attrs ctx)
      :Product (parse-product node attrs ctx)
      :Product-component (parse-component node attrs ctx)
      :Cell (parse-cell node attrs ctx)
      :Quote (parse-quote node attrs ctx)
      :Relationship (parse-relationship node attrs ctx)
      :Rhetorical (parse-rhetorical node attrs ctx)
      :If-then-else (parse-conditional node attrs ctx)
      :One-of-synonyms (parse-list {:type :Any-of} node attrs ctx)
      :Lexicon (parse-lexicon node attrs ctx)
      :Dictionary-item (parse-dictionary node attrs ctx)
      :AMR (parse-amr node attrs ctx)
      :Thematic-role (parse-themrole node attrs ctx)
      (parse-unknown node))))



