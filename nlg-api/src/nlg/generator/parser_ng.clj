(ns nlg.generator.parser-ng
  (:require [clojure.tools.logging :as log]
            [nlg.generator.ops :as ops]
            [nlg.generator.amr :as amr-utils]
            [clojure.string :as str]
            [nlg.api.dictionary :as dictionary-api]
            [nlg.generator.realizer :as realizer]
            [data-access.entities.dictionary :as dictionary-entity]
            [data-access.entities.amr :as amr-entity]))

(def parse-cnt (atom 0))
(defn reset-parse-cnt [] (reset! parse-cnt 0))

(defn node-plus-children [node children attrs ctx]
  (cons node children))

(declare parse-node)

(defn parse-children
  [children attrs ctx]
  (map #(parse-node % attrs ctx) children))

(defn parse-segment
  [{:keys [children]} attrs ctx]
  (parse-children children attrs ctx))

(defn parse-component
  [{:keys [name children]} attrs ctx]
  (cons
     (parse-node name (assoc attrs :type :component) ctx)
     (parse-children children attrs ctx)))


(defn parse-product
  [{:keys [name children]} attrs ctx]
  (cons
   (parse-node name (assoc attrs :type :product) ctx)
   (flatten (parse-children children attrs ctx))))

(defn parse-relationship
  [{:keys [relationshipType children]} attrs ctx]
  (cons
   (ops/append-static (ops/get-word relationshipType) ctx)
   (parse-children children (assoc attrs :type :benefit) ctx)))

(defn parse-dictionary
  [{:keys [name]} attrs ctx]
  (let [reader-profile (ctx :reader-profile)
        words (dictionary-api/search (str/lower-case name) reader-profile)]
    (map #(ops/append-dynamic % (assoc attrs :type :wordlist :class name) ctx) words)))

(defn parse-cell
  [node attrs ctx]
  (ops/append-dynamic {:cell (keyword (node :name))
                       :dyn-name (format "$%d" (swap! parse-cnt inc))}
                      (assoc attrs :source :cell) ctx))

(defn parse-quote
  [node attrs ctx]
  (ops/append-dynamic {:quote (node :text)
                       :dyn-name (format "$%d" (swap! parse-cnt inc))}
                      (assoc attrs :source :quote) ctx))
  
(defn parse-document-plan
  [{:keys [segments]} attrs ctx]
  (reset-parse-cnt)
  (parse-children segments attrs ctx))

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
  [opts {:keys [children]} attrs ctx]
  (case (opts :type)
    :Any-of (parse-node (rand-nth children) attrs ctx)))

(defn parse-amr
  [{:keys [roles conceptId dictionaryItem]} attrs ctx]
  (let [amr-attrs         (assoc attrs :amr true)
        idx               (swap! parse-cnt inc)
        reader-profile    (ctx :reader-profile)
        vc                (amr-entity/get-verbclass conceptId)
        slots             (map :type (:thematic-roles vc))
        members           (-> dictionaryItem
                              :itemId
                              (dictionary-api/search reader-profile)
                              (ops/wrap-to :name)) ;; Our dictionary-items are becoming grammar's members
        children          (flatten
                           (map (fn [{:keys [name children]}]
                                  (parse-children children (assoc amr-attrs :title name) ctx))
                                roles))
        results           (amr-utils/generate-results
                           (amr-utils/build-grammars vc members)
                           (conj
                            (map amr-utils/to-placeholder slots)
                            (:name (first members))  ;; First verb from our dictionary
                            ))
        restrictors       (amr-utils/build-restrictors vc
                           (amr-utils/amr-keys->data-keys children))]
    (log/debugf "AMR Results: %s" (pr-str results))
    (when (seq? results)
      (cons
       (ops/append-dynamic
        {:quotes (map (fn [[rule restrict]] {:value (ops/replace-multi rule
                                                     (amr-utils/placeholders->dyn-names children))
                                             :gate restrict})
                      (ops/zip results restrictors)) ;; All of the AMR variations are saved as array of quotes
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
      :If-then-else (parse-conditional node attrs ctx)
      :One-of-synonyms (parse-list {:type :Any-of} node attrs ctx)
      :Dictionary-item (parse-dictionary node attrs ctx)
      :AMR (parse-amr node attrs ctx)
      :Thematic-role (parse-themrole node attrs ctx)
      (parse-unknown node))))



