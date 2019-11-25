(ns acc-text.nlg.gf.grammar.function
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::args (s/coll-of string?))

(s/def :statement/type #{:operator :function :literal})

(s/def :statement/value string?)

(s/def ::statement (s/keys :req-un [:statement/type :statement/value]))

(s/def ::body (s/coll-of ::statement))

(s/def ::ret #{[:s "Str"]})

(s/def ::function (s/keys :req [::name ::args ::body ::ret]))

(defn concept->name [{::sg/keys [id type]}]
  (str (->> (str/split (name type) #"-")
            (map str/capitalize)
            (str/join))
       (name id)))

(defn variants [xs]
  (->> xs
       (map (fn [x]
              (concat [{:type  :operator
                        :value "("}]
                      x
                      [{:type  :operator
                        :value ")"}])))
       (interpose {:type  :operator
                   :value "|"})
       (flatten)))

(defmulti build (fn [concept _ _ _] (::sg/type concept)))

(defmethod build :document-plan [concept children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "++"}
                     (for [child-concept children]
                       {:type  :function
                        :value (concept->name child-concept)}))
   ::ret  [:s "Str"]})

(defmethod build :segment [concept children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "++"}
                     (for [child-concept children]
                       {:type  :function
                        :value (concept->name child-concept)}))
   ::ret  [:s "Str"]})

(defmethod build :data [{value ::sg/value :as concept} children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "++"}
                     (concat
                       (for [child-concept children]
                         {:type  :function
                          :value (concept->name child-concept)})
                       [{:type  :literal
                         :value (format "{{%s}}" value)}]))
   ::ret  [:s "Str"]})

(defmethod build :quote [{value ::sg/value :as concept} children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "++"}
                     (concat
                       (for [child-concept children]
                         {:type  :function
                          :value (concept->name child-concept)})
                       [{:type  :literal
                         :value value}]))
   ::ret  [:s "Str"]})

(defmethod build :dictionary-item [{::sg/keys [value attributes] :as concept} children _ {dictionary :dictionary}]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "|"}
                     (concat
                       (for [value (set (cons (::sg/name attributes) (get dictionary value)))]
                         {:type  :literal
                          :value value})
                       (for [child-concept children]
                         {:type  :function
                          :value (concept->name child-concept)})))
   ::ret  [:s "Str"]})

(defmethod build :amr [{value ::sg/value :as concept} children relations {amr :amr}]
  (let [function-concept (some (fn [[role concept]]
                                 (when (= :function role) concept))
                               (zipmap (map ::sg/role relations) children))
        role-map (reduce (fn [m [{role ::sg/role {attr-name ::sg/name} ::sg/attributes} concept]]
                           (cond-> m
                                   (and (not= :function role)
                                        (some? attr-name)) (assoc (str/lower-case attr-name) (concept->name concept))))
                         {}
                         (zipmap relations children))]
    {::name (concept->name concept)
     ::args (map concept->name children)
     ::body (variants
              (for [syntax (->> (keyword value) (get amr) (:frames) (map :syntax))]
                (interpose {:type  :operator
                            :value "++"}
                           (for [{value :value pos :pos} syntax]
                             (let [role (when value (str/lower-case value))]
                               (cond
                                 (contains? role-map role) {:type  :function
                                                            :value (get role-map role)}
                                 (and (some? function-concept)
                                      (= pos :VERB)) {:type  :function
                                                      :value (concept->name function-concept)}
                                 (some? value) {:type  :literal
                                                :value value}
                                 :else {:type  :literal
                                        :value "{{...}}"}))))))
     ::ret  [:s "Str"]}))

(defmethod build :sequence [concept children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "++"}
                     (for [child-concept children]
                       {:type  :function
                        :value (concept->name child-concept)}))
   ::ret  [:s "Str"]})

(defmethod build :shuffle [concept children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (variants
            (for [permutation (permutations children)]
              (interpose {:type  :operator
                          :value "++"}
                         (for [child-concept permutation]
                           {:type  :function
                            :value (concept->name child-concept)}))))
   ::ret  [:s "Str"]})

(defmethod build :synonyms [concept children _ _]
  {::name (concept->name concept)
   ::args (map concept->name children)
   ::body (interpose {:type  :operator
                      :value "|"}
                     (for [child-concept children]
                       {:type  :function
                        :value (concept->name child-concept)}))
   ::ret  [:s "Str"]})

(s/fdef build
        :args (s/cat :concept ::sg/concept
                     :children ::sg/concepts
                     :relations ::sg/relations
                     :context map?)
        :ret ::function)
