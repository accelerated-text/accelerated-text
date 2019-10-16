(ns api.nlg.generator.parser-ng
  (:require [api.nlg.dictionary :as dictionary-api]
            [api.nlg.generator.amr :as amr-utils]
            [api.nlg.generator.ops :as ops]
            [api.nlg.generator.realizer :as realizer]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.amr :as amr-entity]))

(def synset
  {:provides    ["provides"]
   :consequence ["results"]})

(defn node-plus-children [node children _ _]
  (cons node children))

(declare parse-node)

(defn parse-children
  [children attrs ctx parse-cnt]
  (map #(parse-node % attrs ctx parse-cnt)
       (remove nil? children)))

(defn parse-segment
  [{:keys [children]} attrs ctx parse-cnt]
  (parse-children children attrs ctx parse-cnt))

(defn parse-component
  [{:keys [name children]} attrs ctx parse-cnt]
  (cons
    (parse-node name (assoc attrs :type :component) ctx parse-cnt)
    (parse-children children attrs ctx parse-cnt)))

(defn parse-product
  [{:keys [name children]} attrs ctx parse-cnt]
  (cons
    (parse-node name (assoc attrs :type :product) ctx parse-cnt)
    (flatten (parse-children children attrs ctx parse-cnt))))

(defn parse-relationship
  [{:keys [relationshipType children]} attrs ctx parse-cnt]
  (cons
    (let [word (rand-nth (seq (get synset (keyword relationshipType))))]
      (ops/append-static word ctx))
    (parse-children children (assoc attrs :type :benefit) ctx parse-cnt)))

(defn parse-dictionary
  [{:keys [name]} attrs ctx]
  (let [reader-profile (:reader-profile ctx)
        words (dictionary-api/search (str/lower-case name) reader-profile)]
    (map #(ops/append-dynamic % (assoc attrs :type :wordlist :class name) ctx) words)))

(defn parse-dictionary-item-modifier
  [{:keys [child name]} attrs ctx parse-cnt]
  (cons
    (ops/append-dynamic {:modifier name
                         :dyn-name (format "$%d" (swap! parse-cnt inc))}
                        ; FIXME
                        (if (:type attrs)
                          (assoc attrs :source :modifier)
                          (assoc attrs :source :modifier :type :modifier))
                        ctx)
    (parse-children [child] attrs ctx parse-cnt)))

(defn parse-cell
  [node attrs ctx parse-cnt]
  (ops/append-dynamic {:cell     (keyword (:name node))
                       :dyn-name (format "$%d" (swap! parse-cnt inc))}
                      ; FIXME
                      (if (:type attrs)
                        (assoc attrs :source :cell)
                        (assoc attrs :source :cell :type :cell))
                      ctx))

(defn parse-quote
  [node attrs ctx parse-cnt]
  (ops/append-dynamic {:quote    (:text node)
                       :dyn-name (format "$%d" (swap! parse-cnt inc))}
                      (assoc attrs :source :quote) ctx))

(defn parse-document-plan
  [{:keys [segments]} attrs ctx]
  (let [parse-cnt (atom 0)
        res (parse-children segments attrs ctx parse-cnt)]
    ; FIXME
    (if (-> res first first :dynamic)
      [res]
      res)))

(defn parse-if-statement
  [{:keys [operator type value1 value2]} attrs ctx parse-cnt]
  (let [cond-fn (case (keyword operator)
                  :== (partial =)
                  := (partial =))]
    (case (keyword type)
      :Value-comparison (fn [data]
                          (let [v1 (-> (parse-node value1 attrs ctx parse-cnt)
                                       (get :dynamic)
                                       (first)
                                       (realizer/get-value data))
                                v2 (-> (parse-node value2 attrs ctx parse-cnt)
                                       (get :dynamic)
                                       (first)
                                       (realizer/get-value data))]
                            (log/tracef "Comparing: '%s' vs '%s'" v1 v2)
                            (cond-fn v1 v2))))))

(defn build-default-cond
  [node attrs ctx gate parse-cnt]
  (parse-node (:thenExpression node) (assoc attrs :gate gate) ctx parse-cnt))

(defn build-if-cond
  [node attrs ctx parse-cnt]
  (parse-node (:thenExpression node) (assoc attrs :gate (parse-if-statement (:condition node) attrs ctx parse-cnt)) ctx parse-cnt))

(defn parse-conditional
  [node attrs ctx parse-cnt]
  (let [conditions (:conditions node)
        ;; All of the If Statements
        usual-conds (filter #(= :If-condition (keyword (:type %))) conditions)
        ;; All of the previous IFs must be false in order for else to be true
        group-negation (fn [data] (not-any? #(% data)
                                            (map #(parse-if-statement (% :condition) attrs ctx parse-cnt) usual-conds)))
        ;; Else statement
        default-conds (filter #(= :Default-condition (keyword (:type %))) conditions)
        block (flatten (map #(build-if-cond % attrs ctx parse-cnt) usual-conds))]
    (if (empty? default-conds)
      block
      (flatten (conj block (build-default-cond (first default-conds) attrs ctx group-negation parse-cnt))))))

(defn parse-list
  [opts {:keys [children]} attrs ctx parse-cnt]
  (case (opts :type)
    :Any-of (parse-node (rand-nth children) attrs ctx parse-cnt)))

(defn parse-amr
  [{:keys [roles conceptId dictionaryItem]} attrs ctx parse-cnt]
  (let [amr-attrs (assoc attrs :amr true)
        idx (swap! parse-cnt inc)
        reader-profile (:reader-profile ctx)
        vc (amr-entity/get-verbclass conceptId)
        slots (map :type (:thematic-roles vc))
        members (for [item (dictionary-api/search (:itemId dictionaryItem) reader-profile)]
                  {:name item})                             ;; Our dictionary-items are becoming grammar's members
        children (flatten
                   (map (fn [{:keys [name children]}]
                          (parse-children children (assoc amr-attrs :title name) ctx parse-cnt))
                        roles))
        results (amr-utils/generate-results
                  (amr-utils/build-grammars vc members)
                  (conj
                    (map amr-utils/to-placeholder slots)
                    ;; First verb from our dictionary
                    (:name (first members))))
        restrictors (amr-utils/build-restrictors vc (amr-utils/amr-keys->data-keys children))]
    (log/debugf "AMR Results: %s" (pr-str results))
    (when (seq? results)
      (cons
        (ops/append-dynamic
          {:quotes   (flatten
                       (map (fn [rules restrict]            ;; All of the AMR variations are saved as array of quotes
                              (for [rule rules]
                                {:gate  restrict
                                 :value (->> children
                                             (amr-utils/placeholders->dyn-names)
                                             (ops/replace-multi rule))}))
                            results restrictors))
           :dyn-name (format "$%d" idx)}
          (assoc attrs :source :quotes :type :amr)
          ctx)
        children))))

(defn parse-themrole
  [node attrs ctx parse-cnt]
  (map #(parse-node % attrs ctx parse-cnt) (:children node)))

(defn parse-unknown
  [node]
  (log/debugf "Unknown node: %s" node))

(defn parse-node
  [node attrs ctx parse-cnt]
  (case (keyword (:type node))
    :Document-plan (parse-document-plan node attrs ctx)
    :Segment (parse-segment node attrs ctx parse-cnt)
    :Product (parse-product node attrs ctx parse-cnt)
    :Product-component (parse-component node attrs ctx parse-cnt)
    :Cell (parse-cell node attrs ctx parse-cnt)
    :Dictionary-item-modifier (parse-dictionary-item-modifier node attrs ctx parse-cnt)
    :Quote (parse-quote node attrs ctx parse-cnt)
    :Relationship (parse-relationship node attrs ctx parse-cnt)
    :If-then-else (parse-conditional node attrs ctx parse-cnt)
    :One-of-synonyms (parse-list {:type :Any-of} node attrs ctx parse-cnt)
    :Dictionary-item (parse-dictionary node attrs ctx)
    :AMR (parse-amr node attrs ctx parse-cnt)
    :Thematic-role (parse-themrole node attrs ctx parse-cnt)
    (parse-unknown node)))
