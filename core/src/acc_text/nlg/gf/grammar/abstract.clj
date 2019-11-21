(ns acc-text.nlg.gf.grammar.abstract
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.tools.logging :as log]))

(defn concept->name [{id ::sg/id type ::sg/type}]
  (format "%s-%s" (name type) (name id)))

;; (defn build-grammar-function [{type ::sg/type :as concept} concept]
;;   #::grammar{:function-name (concept->name concept)
;;              :arguments     (map ::sg/role relations)
;;              :return        type})
(defn build-grammar-function [relations parent]
  #::grammar{:function-name (concept->name parent)
             :arguments     (map ::sg/role relations)
             :return        (::sg/type parent)})

(defn build [name {relations ::sg/relations concepts ::sg/concepts}]
  (let [concept-map (group-by ::sg/id concepts)
        relation-map (group-by ::sg/from relations)]
    #::grammar{:module-name name
               ;;Start category is always :document-plan
               :flags       {:startcat :document-plan}
               :categories  (->> concepts (map ::sg/type) (sort) (set))
               :functions   (concat
                              (map (fn [[from edges]]
                                     (build-grammar-function edges (first (get concept-map from))))
                                   relation-map)
                              (->> concepts
                                   (filter #(get #{:data :dictionary-item} (::sg/type %)))
                                   (map #(build-grammar-function [] %))))})

  #_(let [relation-map (group-by ::sg/from relations)]
    #::grammar{:module-name name
               ;;Start category is always :document-plan
               :flags       {:startcat :document-plan}
               :categories  (->> concepts (map ::sg/type) (sort) (dedupe))
               :functions   (map (fn [{id ::sg/id :as concept}]
                                   (build-grammar-function concept (get relation-map id)))
                                 concepts)}))
