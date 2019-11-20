(ns acc-text.nlg.gf.grammar.concrete
  (:require [acc-text.nlg.gf.grammar.abstract :refer [concept->name]]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]
            [acc-text.nlg.gf.string-utils :as su]))

(defmulti build-lin (fn [{type ::sg/type} _ _ _] type))

(defmethod build-lin :document-plan [concept children _ _]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (for [child-concept children]
                                             #:acc-text.nlg.gf.grammar{:role  :function
                                                                       :value (concept->name child-concept)})})

(defmethod build-lin :segment [concept children _ _]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (for [child-concept children]
                                             #:acc-text.nlg.gf.grammar{:role  :function
                                                                       :value (concept->name child-concept)})})

(defmethod build-lin :data [{id ::sg/id value ::sg/value :as concept} children _ _]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (concat
                                             (for [child-concept children]
                                               #:acc-text.nlg.gf.grammar{:role  :function
                                                                         :value (concept->name child-concept)})
                                             [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                        :value (format "{{%s}}" value)}])})

(defmethod build-lin :quote [{id ::sg/id value ::sg/value :as concept} children _ _]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (concat
                                             (for [child-concept children]
                                               #:acc-text.nlg.gf.grammar{:role  :function
                                                                         :value (concept->name child-concept)})
                                             [#:acc-text.nlg.gf.grammar{:role  :literal
                                                                        :value (su/escape-string value)}])})

(defmethod build-lin :dictionary-item [{id ::sg/id value ::sg/value {attr-name ::sg/name} ::sg/attributes :as concept} children _ {dictionary :dictionary}]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (concat
                                             (for [value (set (cons attr-name (get dictionary value)))]
                                               #:acc-text.nlg.gf.grammar{:role  :literal
                                                                         :value (su/escape-string value)})
                                             (for [child-concept children]
                                               #:acc-text.nlg.gf.grammar{:role  :function
                                                                         :value (concept->name child-concept)}))})

(defmethod build-lin :amr [{id ::sg/id value ::sg/value :as concept} children relations {amr :amr}]
  (let [function-concept (some (fn [[role concept]]
                                 (when (= :function role) concept))
                               (zipmap (map ::sg/role relations) children))
        role-map (reduce (fn [m [{to ::sg/to role ::sg/role {attr-name ::sg/name} ::sg/attributes} concept]]
                           (cond-> m
                                   (and (not= :function role)
                                        (some? attr-name)) (assoc (str/lower-case attr-name) (concept->name concept))))
                         {}
                         (zipmap relations children))]
    (for [[i syntax] (zipmap (rest (range)) (->> (keyword value) (get amr) (:frames) (map :syntax)))]
      #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                                :syntax        (for [{value :value pos :pos} syntax]
                                                 (let [role (when value (str/lower-case value))]
                                                   (cond
                                                     (contains? role-map role) #:acc-text.nlg.gf.grammar{:role  :function
                                                                                                         :value (get role-map role)}
                                                     (some? value) #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                             :value (su/escape-string value)}
                                                     (= pos :VERB) #:acc-text.nlg.gf.grammar{:role  :function
                                                                                             :value (concept->name function-concept)}
                                                     :else #:acc-text.nlg.gf.grammar{:role  :literal
                                                                                     :value ""})))})))

(defmethod build-lin :sequence [{id ::sg/id :as concept} children _ _]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (for [child-concept children]
                                             #:acc-text.nlg.gf.grammar{:role  :function
                                                                       :value (concept->name child-concept)})})

(defmethod build-lin :shuffle [{id ::sg/id :as concept} children _ _]
  (for [child-permutation (permutations children)]
    #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                              :syntax        (for [child-concept child-permutation]
                                               #:acc-text.nlg.gf.grammar{:role  :function
                                                                         :value (concept->name child-concept)})}))

(defmethod build-lin :sequence [{id ::sg/id :as concept} children _ _]
  #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                            :syntax        (for [child-concept children]
                                             #:acc-text.nlg.gf.grammar{:role  :function
                                                                       :value (concept->name child-concept)})})

(defmethod build-lin :synonyms [{id ::sg/id :as concept} children _ _]
  (for [child-concept children]
    #:acc-text.nlg.gf.grammar{:function-name (concept->name concept)
                              :syntax        [#:acc-text.nlg.gf.grammar{:role  :function
                                                                        :value (concept->name child-concept)}]}))

(defn build [parent-name name {relations ::sg/relations concepts ::sg/concepts} context]
  #:acc-text.nlg.gf.grammar{:module-name name
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
