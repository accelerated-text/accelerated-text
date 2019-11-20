(ns acc-text.nlg.gf.grammar.abstract
  (:require [acc-text.nlg.semantic-graph :as sg]))

(defn concept->name [{id ::sg/id type ::sg/type}]
  (format "%s-%s" (name type) (name id)))

(defn build-grammar-function [{type ::sg/type :as concept} relations]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :arguments     (map ::sg/role relations)
                            :return        type})

(defn build [name {relations ::sg/relations concepts ::sg/concepts}]
  (let [concept-map (zipmap (map ::sg/id concepts) concepts)
        relation-map (group-by ::sg/from relations)]
    #:acc-text.nlg.gf.grammar{:module-name name
                              :flags       {:startcat (when (seq concepts) (::sg/type (first concepts)))}
                              :categories  (->> concepts (map ::sg/type) (sort) (dedupe))
                              :functions   (map (fn [{id ::sg/id :as concept}]
                                                  (build-grammar-function concept (get relation-map id)))
                                                concepts)}))
