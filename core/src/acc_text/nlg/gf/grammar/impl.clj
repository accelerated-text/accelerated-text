(ns acc-text.nlg.gf.grammar.impl
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]))

(defn concept->name [{::sg/keys [id type]}]
  (str (->> (str/split (name type) #"-")
            (map str/capitalize)
            (str/join))
       (name id)))

(defn variants [xs]
  (if-not (< 1 (count xs))
    (first xs)
    (->> xs
         (map (fn [x]
                (concat [{:type  :operator
                          :value "("}]
                        x
                        [{:type  :operator
                          :value ")"}])))
         (interpose {:type  :operator
                     :value "|"})
         (flatten))))

(defn get-child-with-role [concepts relations role]
  (some (fn [[relation concept]]
          (when (= role (::sg/role relation)) concept))
        (zipmap relations concepts)))

(defn attach-selectors [m attrs]
  (let [selectors (->> (keys attrs) (remove #{:pos :role :value}) (select-keys attrs))]
    (cond-> m (seq selectors) (assoc :selectors selectors))))

(defmulti build-function (fn [concept _ _ _] (::sg/type concept)))

(defmethod build-function :default [concept children _ _]
  {:name   (concept->name concept)
   :params (map concept->name children)
   :body   (interpose {:type  :operator
                       :value "++"}
                      (for [child-concept children]
                        {:type  :function
                         :value (concept->name child-concept)}))
   :ret    [:s "Str"]})

(defmethod build-function :data [{value ::sg/value :as concept} _ _ _]
  {:name   (concept->name concept)
   :params []
   :body   [{:type  :literal
             :value (format "{{%s}}" value)}]
   :ret    [:s "Str"]})

(defmethod build-function :quote [{value ::sg/value :as concept} _ _ _]
  {:name   (concept->name concept)
   :params []
   :body   [{:type  :literal
             :value value}]
   :ret    [:s "Str"]})

(defmethod build-function :dictionary-item [{::sg/keys [value attributes] :as concept} _ _ {dictionary :dictionary}]
  {:name   (concept->name concept)
   :params []
   :body   (interpose {:type  :operator
                       :value "|"}
                      (for [value (set (cons (::sg/name attributes) (get dictionary value)))]
                        {:type  :literal
                         :value value}))
   :ret    [:s "Str"]})

(defmethod build-function :modifier [concept children relations _]
  (let [child-concept (get-child-with-role children relations :child)
        modifier-concepts (remove #(= (::sg/id child-concept) (::sg/id %)) children)]
    {:name   (concept->name concept)
     :params (map concept->name children)
     :body   (interpose {:type  :operator
                         :value "++"}
                        (cond-> (map (fn [modifier-concept]
                                       {:type  :function
                                        :value (concept->name modifier-concept)})
                                     modifier-concepts)
                                (some? child-concept) (concat [{:type  :function
                                                                :value (concept->name child-concept)}])))
     :ret    [:s "Str"]}))

(defmethod build-function :amr [{value ::sg/value :as concept} children relations {amr :amr}]
  (let [function-concept (get-child-with-role children relations :function)
        role-map (reduce (fn [m [{role ::sg/role {attr-name ::sg/name} ::sg/attributes} concept]]
                           (cond-> m
                                   (and (not= :function role)
                                        (some? attr-name)) (assoc (str/lower-case attr-name) (concept->name concept))))
                         {}
                         (zipmap relations children))]
    {:name   (concept->name concept)
     :params (map concept->name children)
     :body   (variants
               (for [syntax (->> (keyword value) (get amr) (:frames) (map :syntax))]
                 (interpose {:type  :operator
                             :value "++"}
                            (for [{value :value pos :pos role :role :as attrs} syntax]
                              (let [role-key (when (some? role) (str/lower-case role))]
                                (-> (cond
                                      (contains? role-map role-key) {:type  :function
                                                                     :value (get role-map role-key)}
                                      (some? role) {:type  :literal
                                                    :value (format "{{%s}}" role)}
                                      (= pos :AUX) {:type  :function
                                                    :value "(copula Sg)"}
                                      (and (some? function-concept)
                                           (= pos :VERB)) {:type  :function
                                                           :value (concept->name function-concept)}
                                      (some? value) {:type  :literal
                                                     :value value}
                                      :else {:type  :literal
                                             :value "{{...}}"})
                                    (attach-selectors attrs)
                                    (assoc :pos pos)))))))
     :ret    [:s "Str"]}))

(defmethod build-function :shuffle [concept children _ _]
  {:name   (concept->name concept)
   :params (map concept->name children)
   :body   (variants
             (for [permutation (permutations children)]
               (interpose {:type  :operator
                           :value "++"}
                          (for [child-concept permutation]
                            {:type  :function
                             :value (concept->name child-concept)}))))
   :ret    [:s "Str"]})

(defmethod build-function :synonyms [concept children _ _]
  {:name   (concept->name concept)
   :params (map concept->name children)
   :body   (interpose {:type  :operator
                       :value "|"}
                      (for [child-concept children]
                        {:type  :function
                         :value (concept->name child-concept)}))
   :ret    [:s "Str"]})

(defmethod build-function :reference [concept children _ _]
  {:name   (concept->name concept)
   :params (map concept->name children)
   :body   (interpose {:type  :operator
                       :value "|"}
                      (for [child-concept children]
                        {:type  :function
                         :value (concept->name child-concept)}))
   :ret    [:s "Str"]})

(defn build-grammar [module instance {::sg/keys [concepts relations]} context]
  #:acc-text.nlg.gf.grammar{:module   module
                            :instance instance
                            :flags    {:startcat (concept->name (first concepts))}
                            :syntax   (let [concept-map (zipmap (map ::sg/id concepts) concepts)
                                            relation-map (group-by ::sg/from relations)]
                                        (map (fn [{id ::sg/id :as concept}]
                                               (let [relations (get relation-map id)
                                                     children (map (comp concept-map ::sg/to) relations)]
                                                 (build-function concept children relations context)))
                                             concepts))})
