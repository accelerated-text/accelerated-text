(ns acc-text.nlg.gf.builder
  (:require [acc-text.nlg.gf.cf-format :as cf]
            [acc-text.nlg.gf.semantic-graph-utils :as sg-utils]
            [acc-text.nlg.spec.semantic-graph :as sg]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(defn modifier->gf [semantic-graph concept-table]
  (let [modifiers (sg-utils/relations-with-concepts semantic-graph concept-table :modifier)]
    (when (seq modifiers)
      (map (fn [[_ {{name ::sg/name} ::sg/attributes}]] (cf/gf-morph-item name "A" name))
           modifiers))))

(defn data->cf [semantic-graph]
  (map-indexed (fn [idx {value ::sg/value :as concept}]
         (if (< 1 (count (::sg/concepts (sg-utils/subgraph semantic-graph concept))))
           ;; If we have modifiers create 'A NP'
           (cf/gf-modified-morph-item value "NP" "A" value)
           ;; Else just plain NP
           (cf/gf-morph-item value "NP" (cf/data-morphology-value value)))
       (sg-utils/concepts-with-type semantic-graph :data)))

(defn quote->gf [semantic-graph]
  (map (fn [{value ::sg/value}] (cf/gf-morph-item "Quote" "S" value))
       (sg-utils/concepts-with-type semantic-graph :quote)))

(defn amr->gf [semantic-graph concept-table]
  (let [functions (map second (sg-utils/relations-with-concepts semantic-graph concept-table :function))]
    (map (fn [{type ::sg/type :as concept}]
           (cond
             (= :dictionary-item type) (let [name (get-in concept [::sg/attributes ::sg/name])
                                             members (::sg/members concept)
                                             item (when (seq members) (rand-nth members))]
                                         (cf/gf-morph-item (str name "amr" )"V2" (or item name)))))
         functions)))

(defn frame->cf
  ;; Takes single syntax and converts to CF grammar row
  ;; syntax-type: string by which it will be refered in grammar, eg. `AMR1`
  ;; themrole-idx: Mapping for values. Eg. `Agent` should be `NP0`, this map shows what value to use inside grammar
  ;; syntax: syntax directly from verbnet frame
  [syntax-type themrole-idx syntax]
  (cf/gf-syntax-item "AMR" syntax-type (string/join " " (map
                                                           (fn
                                                             [{:keys [pos value]}]
                                                             (case pos
                                                               :NP (get themrole-idx value)
                                                               :LEX (format "\"%s\"" value)
                                                               :VERB "V2" ;; We may need similar mapping as for NP, in case there would be more than one verb
                                                               :PREP (format "\"%s\"" value)))
                                                           syntax))))

(defn amr->table [semantic-graph data-table]
  (let [counter (atom 0)
        mapper (into {} (map (fn [{:keys [key symbol]}] [key symbol]) data-table))]
    (map
     (fn [{value ::sg/value {syntax ::sg/syntax} ::sg/attributes}]
       (map
        (fn [frame]
          (let [idx (swap! counter inc)
                s   (format "VP%d" idx)]
            {:index idx
             :symbol s
             :value value
             :syntax (frame->cf s mapper frame)
             :mapper mapper}))
        syntax))
     (sg-utils/concepts-with-type semantic-graph :amr))))

(defn amr-table->gf [amr-table]
  amr-table)

;; Those are predefined heads of grammar tree, they will differ
;; based on what type of phrase begins the text.
(def gf-head-trees {:np [(cf/gf-syntax-item "Phrase" "S" "NP")]
                    :vp [(cf/gf-syntax-item "Phrase" "S" "NP VP")
                         (cf/gf-syntax-item "ComplV2" "VP" "V2 NP")]
                    :ap [(cf/gf-syntax-item "Phrase" "S" "NP")]})

(defn start-category->gf [{relations ::sg/relations concepts ::sg/concepts}]
  ;;in order to decide which GF to generate we do not need complete concept/relation data
  ;;for pattern matching only their types are needed
  (let [concept-pattern (set (map ::sg/type concepts))
        relation-pattern (set (map ::sg/role relations))]
    (cond
      ;;Data concept only graph
      (and (= concept-pattern #{:data}) (empty? relation-pattern))
      (:np gf-head-trees)

      ;;Adverbial phrase only graph
      (and (= concept-pattern #{:data :dictionary-item}) (= relation-pattern #{:modifier}))
      (:ap gf-head-trees)

      ;;Verb phrase
      (contains? concept-pattern :amr)
      (:vp gf-head-trees)

      ;;Probably need to throw an error, we can not have unresolved start cats
      :else nil)))

(defn build-grammar [semantic-graph]
  (let [main-graph (sg-utils/drop-non-semantic-parts semantic-graph)
        concept-table (sg-utils/concepts->concept-map main-graph)]
    (concat
      (start-category->gf main-graph)
      (amr->gf main-graph concept-table)
      (data->gf main-graph)
      (quote->gf main-graph)
      (modifier->gf main-graph concept-table))))

(s/fdef build-grammar
        :args (s/cat :semantic-graph ::sg/graph)
        :ret (s/coll-of string? :min-count 2))

(defn join-grammars
  ;; Joins together syntax generated by grammar, root node and variable declarations creating full CF grammar
  [frames root variables]
  (concat
   (list root)
   frames
   variables))

(defn vn->cf
  ;; Converts verbnet into CF grammar
  ;; NOTE: this creates fully fledged grammar. Usually it should be just a part of our grammar
  [{:keys [members frames thematic-roles]}]
  (let [themrole-idx (into {} (map-indexed (fn [idx {type :type}] [type (format "NP%d" idx)]) thematic-roles))
        root         (cf/gf-syntax-item "Pred" "S" "VP")
        variables    (concat
                      (map (fn [{name :name}] (cf/gf-morph-item "Action" "V2" name)) members)
                      (map (fn [[k v]] (cf/gf-morph-item k v (cf/data-morphology-value k))) themrole-idx))]
    (-> (partial frame->cf "VP" themrole-idx)
        (map frames)
        (join-grammars root variables))))


