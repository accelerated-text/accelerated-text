(ns acc-text.nlg.gf.grammar.concrete
  (:require [acc-text.nlg.gf.grammar.abstract :refer [concept->name]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.gf.grammar :as grammar]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]
            [acc-text.nlg.gf.string-utils :as su]))

(defmulti build-lin (fn [{type ::sg/type} _ _ _] type))

(defmethod build-lin :document-plan [concept children _ _]
  #::grammar{:function-name (concept->name concept)
             :syntax        (for [child-concept children]
                              #::grammar{:role  :function
                                         :value (concept->name child-concept)})})

(defmethod build-lin :segment [concept children _ _]
  #::grammar{:function-name (concept->name concept)
             :syntax        (for [child-concept children]
                              #::grammar{:role  :function
                                         :value (concept->name child-concept)})})

(defmethod build-lin :data [{value ::sg/value :as concept} children _ _]
  #::grammar{:function-name (concept->name concept)
             :syntax        (concat
                             (for [child-concept children]
                               #::grammar{:role  :function
                                          :value (concept->name child-concept)})
                             [#::grammar{:role  :literal
                                         :value (format "{{%s}}" value)}])})

(defmethod build-lin :quote [{value ::sg/value :as concept} children _ _]
  #::grammar{:function-name (concept->name concept)
             :syntax        (concat
                             (for [child-concept children]
                               #::grammar{:role  :function
                                          :value (concept->name child-concept)})
                             [#::grammar{:role  :literal
                                         :value (su/escape-string value)}])})

(defmethod build-lin :dictionary-item [{value ::sg/value {attr-name ::sg/name} ::sg/attributes :as concept} children _ {dictionary :dictionary}]
  #::grammar{:function-name (concept->name concept)
             :syntax        (concat
                             (for [value (set (cons attr-name (get dictionary value)))]
                               #::grammar{:role  :literal
                                          :value (su/escape-string value)})
                             (for [child-concept children]
                               #::grammar{:role  :function
                                          :value (concept->name child-concept)}))})

(defmethod build-lin :amr [{value ::sg/value :as concept} children relations {amr :amr}]
  (let [function-concept (some (fn [[role concept]]
                                 (when (= :function role) concept))
                               (zipmap (map ::sg/role relations) children))
        role-map (reduce (fn [m [{role ::sg/role {attr-name ::sg/name} ::sg/attributes} concept]]
                           (cond-> m
                             (and (not= :function role)
                                  (some? attr-name)) (assoc (str/lower-case attr-name) (concept->name concept))))
                         {}
                         (zipmap relations children))]
    (for [syntax (->> (keyword value) (get amr) (:frames) (map :syntax))]
      #::grammar{:function-name (concept->name concept)
                 :syntax        (for [{value :value pos :pos} syntax]
                                  (let [role (when value (str/lower-case value))]
                                    (cond
                                      (contains? role-map role) #::grammar{:role  :function
                                                                           :value (get role-map role)}
                                      (some? value) #::grammar{:role  :literal
                                                               :value (su/escape-string value)}
                                      (= pos :VERB) #::grammar{:role  :function
                                                               :value (concept->name function-concept)}
                                      :else #::grammar{:role  :literal
                                                       :value ""})))})))

(defmethod build-lin :sequence [concept children _ _]
  #::grammar{:function-name (concept->name concept)
             :syntax        (for [child-concept children]
                              #::grammar{:role  :function
                                         :value (concept->name child-concept)})})

(defmethod build-lin :shuffle [concept children _ _]
  (for [child-permutation (permutations children)]
    #::grammar{:function-name (concept->name concept)
               :syntax        (for [child-concept child-permutation]
                                #::grammar{:role  :function
                                           :value (concept->name child-concept)})}))

(defmethod build-lin :sequence [concept children _ _]
  #::grammar{:function-name (concept->name concept)
             :syntax        (for [child-concept children]
                              #::grammar{:role  :function
                                         :value (concept->name child-concept)})})

(defmethod build-lin :synonyms [concept children _ _]
  (for [child-concept children]
    #::grammar{:function-name (concept->name concept)
               :syntax        [#::grammar{:role  :function
                                          :value (concept->name child-concept)}]}))

(defn build [parent-name name {relations ::sg/relations concepts ::sg/concepts} context]
  #::grammar{:module-name name
             :of          parent-name
             :lin-types   (reduce (fn [m {type ::sg/type}]
                                    (assoc m type [:s :str]))
                                  {}
                                  concepts)
             :lins        (let [concept-map (zipmap (map ::sg/id concepts) concepts)
                                relation-map (group-by ::sg/from relations)]
                            (flatten
                             (map (fn [{id ::sg/id :as concept}]
                                    (let [relations (get relation-map id)
                                          children (map #(get concept-map (::sg/to %)) relations)]
                                      (build-lin concept children relations context)))
                                  concepts)))})
