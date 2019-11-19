(ns acc-text.nlg.gf.grammar.abstract
  (:require [acc-text.nlg.semantic-graph :as sg]))

(defn get-category [{type ::sg/type}]
  (case type
    :document-plan "Document"
    :segment "Segment"
    :amr "AMR"
    :data "Data"
    :quote "Quote"
    :dictionary-item "DictionaryItem"
    :sequence "Sequence"
    :shuffle "Shuffle"
    :synonyms "Synonyms"))

(defn build-function [{id ::sg/id :as concept} children]
  (let [category (get-category concept)]
    #:acc-text.nlg.gf.grammar{:function-name (str category (name id))
                              :arguments     (map get-category children)
                              :return        category}))

(defn build [name {relations ::sg/relations concepts ::sg/concepts}]
  (let [concept-map (zipmap (map ::sg/id concepts) concepts)
        relation-map (group-by ::sg/from relations)]
    #:acc-text.nlg.gf.grammar{:module-name name
                              :flags       {:startcat (when (seq concepts) (get-category (first concepts)))}
                              :categories  (->> concepts (map get-category) (sort) (dedupe))
                              :functions   (map (fn [{id ::sg/id :as concept}]
                                                  (let [relations (get relation-map id)
                                                        children (map (fn [{to ::sg/to}] (get concept-map to)) relations)]
                                                    (build-function concept children)))
                                                concepts)}))
