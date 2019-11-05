(ns acc-text.nlg.gf.builder
  (:require [acc-text.nlg.gf.cf-format :as cf]
            [acc-text.nlg.gf.semantic-graph-utils :as sg-utils]
            [acc-text.nlg.spec.semantic-graph :as sg]))

(defn modifier->gf [semantic-graph concept-table]
  (let [modifiers (sg-utils/relations-with-concepts semantic-graph concept-table :modifier)]
    (when (seq modifiers)
      (map (fn [[_ {{name ::sg/name} ::sg/attributes}]] (cf/gf-morph-item name "A" name))
           modifiers))))

(defn data->gf [semantic-graph]
  (map (fn [{value ::sg/value}] (cf/gf-morph-item value "NP" (cf/data-morphology-value value)))
       (sg-utils/concepts-with-type semantic-graph :data)))

(defn amr->gf [semantic-graph]
  (map (fn [{value ::sg/value}] (cf/gf-morph-item value "V2" value))
       (sg-utils/concepts-with-type semantic-graph :amr)))

(def gf-head-trees {:np [(cf/gf-syntax-item "Phrase" "S" "NP")]
                    :vp [(cf/gf-syntax-item "Phrase" "S" "NP VP")
                         (cf/gf-syntax-item "Compl-v" "VP" "V2 NP")]
                    :ap [(cf/gf-syntax-item "Phrase" "S" "AP")
                         (cf/gf-syntax-item "Compl-a" "AP" "A NP")]})

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
      (= concept-pattern #{:amr :data})
      (:vp gf-head-trees)

      ;;Probably need to throw an error, we can not have unresolved start cats
      :else nil)))

(defn generate-grammar [semantic-graph-instance]
  (let [main-graph (sg-utils/drop-non-semantic-parts semantic-graph-instance)
        concept-table (sg-utils/concepts->concept-map main-graph)]
    (concat
      (start-category->gf main-graph)
      (amr->gf main-graph)
      (data->gf main-graph)
      (modifier->gf main-graph concept-table))))
