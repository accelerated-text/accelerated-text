(ns acc-text.nlg.gf.grammar.abstract
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph :as sg]))

(defn concept->name [{id ::sg/id type ::sg/type}]
  (format "%s-%s" (name type) (name id)))

(defn build-grammar-function [{type ::sg/type :as concept} relations]
  #::grammar{:function-name (concept->name concept)
             :arguments     (map ::sg/role relations)
             :return        type})

(defn build [name {relations ::sg/relations concepts ::sg/concepts}]
  (let [relation-map (group-by ::sg/from relations)]
    #::grammar{:module-name name
               :flags       {:startcat (when (seq concepts) (::sg/type (first concepts)))}
               :categories  (->> concepts (map ::sg/type) (sort) (dedupe))
               :functions   (map (fn [{id ::sg/id :as concept}]
                                   (build-grammar-function concept (get relation-map id)))
                                 concepts)}))
