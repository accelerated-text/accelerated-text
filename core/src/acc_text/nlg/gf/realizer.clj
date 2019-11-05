(ns acc-text.nlg.gf.realizer
  (:require [acc-text.nlg.gf.cf-format :as cf]
            [acc-text.nlg.gf.semantic-graph :as sg]))

(defn modifier->gf [semantic-graph concept-table]
  (let [modifiers (sg/relations-with-concepts semantic-graph concept-table :modifier)]
    (when (seq modifiers)
      (map (fn [[_ {{name :name} :attributes}]] (cf/gf-morph-item name "A" name))
           modifiers))))

(defn data->gf [semantic-graph]
  (map (fn [{value :value}] (cf/gf-morph-item value "NP" (cf/data-morphology-value value)))
       (sg/concepts-with-type semantic-graph :data)))

(defn amr->gf [semantic-graph]
  (map (fn [{value :value}] (cf/gf-morph-item value "V2" value))
       (sg/concepts-with-type semantic-graph :amr)))

(def gf-head-trees {:np [(cf/gf-syntax-item "Phrase" "S" "NP")]
                    :vp [(cf/gf-syntax-item "Phrase" "S" "NP VP")
                         (cf/gf-syntax-item "Compl-v" "VP" "V2 NP")]
                    :ap [(cf/gf-syntax-item "Phrase" "S" "AP")
                         (cf/gf-syntax-item "Compl-a" "AP" "A NP")]})

(defn start-category->gf [{:keys [relations concepts]}]
  ;;in order to decide which GF to generate we do not need complete concept/relation data
  ;;for pattern matching only their types are needed
  (let [concept-pattern  (set (map :type concepts))
        relation-pattern (set (map :role relations))]
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

(defn dp->grammar [dp]
  (let [sem-graph (sg/drop-non-semantic-parts dp)
        concept-table (sg/concepts->id-concept sem-graph)]
    (concat
      (start-category->gf sem-graph)
      (amr->gf sem-graph)
      (data->gf sem-graph)
      (modifier->gf sem-graph concept-table))))
