(ns acc-text.nlg.gf.grammar.gf-specification
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.tools.logging :as log]))


(defn terminus-concept? [{type ::sg/type}] (get #{:data :dictionary-item} type))

(defn concept->name [role+type+children {in-edge ::sg/role} {id ::sg/id type ::sg/type}]
  (if (and (seq role+type+children) in-edge)
    (format "%s-%s-%s" (name in-edge) (name type) (name id))
    (format "%s-%s" (name type) (name id))))

(defn param->name [[role type]]
  (if role
    (keyword (format "%s-%s" (name role) (name type)))
    type))

(defn in-role+concept-type
  "Build a collection of tuples where first element is
  the role of the incoming relation and the second is the type
  of the concept receiving that relation"
  [concept-map relations]
  (map (fn [{:keys [::sg/to ::sg/role]}]
         [role (->> to (get concept-map) first ::sg/type)])
       relations))

(defn syntax-var [role+type value role]
  {:var-name (param->name role+type)
   :value value
   :role role})

(defn build-grammar-function [role+type-children [incoming-edge] [parent]]
  (let [function-name (concept->name role+type-children incoming-edge parent)]
    (log/debugf "Building abstract function: '%s'" function-name)
    #::grammar{:function-name function-name
               :arguments     (map #(syntax-var % nil nil) role+type-children)
               :return        (syntax-var [(::sg/role incoming-edge) (::sg/type parent)]
                                          nil nil)}))

(defn build [name {relations ::sg/relations concepts ::sg/concepts}]
  (let [concept-map       (group-by ::sg/id concepts)
        relation-map-from (group-by ::sg/from relations)
        relation-map-to   (group-by ::sg/to relations)]
    #::grammar{:module-name name
               ;;Start category is always :document-plan
               :flags       {:startcat :document-plan}
               :categories  (->> relations
                                 (in-role+concept-type concept-map)
                                 (cons
                                   ;;document plan will not be included in r+c results
                                   [nil :document-plan])
                                 (map param->name)
                                 (set))
               :functions   (concat
                             ;;First build functions with parameters
                             (map (fn [[from edges]]
                                    (build-grammar-function
                                      (in-role+concept-type concept-map edges)
                                      (get relation-map-to from)
                                      (get concept-map from)))
                                  relation-map-from)
                             ;;Now build zero functions for terminates like data and dictionary
                             (->> concepts
                                  (filter terminus-concept?)
                                  (map (fn [{id ::sg/id :as c}]
                                         (build-grammar-function
                                           [] (get relation-map-to id) [c])))))}))
