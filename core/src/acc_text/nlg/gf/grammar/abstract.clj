(ns acc-text.nlg.gf.grammar.abstract
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.tools.logging :as log]))

(defn terminus-concept? [{type ::sg/type}] (get #{:data :dictionary-item} type))

(defn concept->name [children {in-edge ::sg/role} {id ::sg/id type ::sg/type}]
  (if (and (seq children) in-edge)
    (format "%s-%s-%s" (name in-edge) (name type) (name id))
    (format "%s-%s" (name type) (name id))))

(defn build-grammar-function [children [incoming-edge] [parent]]
  (let [function-name (concept->name children incoming-edge parent)]
    (log/debugf "Building abstract function: '%s'" function-name)
    #::grammar{:function-name function-name
               :arguments     (map ::sg/type children)
               :return        (::sg/type parent)}))

(defn build [name {relations ::sg/relations concepts ::sg/concepts}]
  (let [concept-map       (group-by ::sg/id concepts)
        relation-map-from (group-by ::sg/from relations)
        relation-map-to   (group-by ::sg/to relations)]
    #::grammar{:module-name name
               ;;Start category is always :document-plan
               :flags       {:startcat :document-plan}
               :categories  (->> concepts (map ::sg/type) (sort) (set))
               :functions   (concat
                             ;;First build functions with parameters
                             (map (fn [[from edges]]
                                    (build-grammar-function (mapcat #(get concept-map (::sg/to %)) edges)
                                                            (get relation-map-to from)
                                                            (get concept-map from)))
                                  relation-map-from)
                             ;;Now build zero functions for terminates like data and dictionary
                             (->> concepts
                                  (filter terminus-concept?)
                                  (map (fn [{id ::sg/id :as c}]
                                         (build-grammar-function [] (get relation-map-to id) [c])))))}))
