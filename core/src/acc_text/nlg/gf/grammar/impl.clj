(ns acc-text.nlg.gf.grammar.impl
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]))

(def data-types #{:data :dictionary-item :quote})

(defn concept->name [{:keys [id type]}]
  (str (->> (str/split (name type) #"-")
            (map str/capitalize)
            (str/join))
       (name id)))

(defn get-params [children]
  (->> children
       (remove #(contains? data-types (:type %)))
       (map concept->name)))

(defn get-kind [{concept-type :type}]
  (if (contains? data-types concept-type) :variable :function))

(defn get-child-with-role [concepts relations role]
  (some (fn [[relation concept]]
          (when (= role (:role relation)) concept))
        (zipmap relations concepts)))

(defn attach-selectors [m attrs]
  (let [selectors (->> (keys attrs) (remove #{:pos :role :params :ret :value :type}) (select-keys attrs))]
    (cond-> m (seq selectors) (assoc :selectors selectors))))

(defn replace-placeholders [text placeholders]
  (when-not (str/blank? text)
    (reduce-kv (fn [s k v]
                 (let [pattern (re-pattern (format "(?i)\\{\\{%s\\}\\}" (name k)))]
                   (str/replace s pattern v)))
               text
               placeholders)))

(defmulti build-variable (fn [concept _] (:type concept)))

(defmethod build-variable :data [{value :value :as concept} {:keys [data types]}]
  (let [name (concept->name concept)]
    {:name  name
     :value [(get data (keyword value))]
     :type  (get types name "Str")}))

(defmethod build-variable :quote [{value :value :as concept} {:keys [data types]}]
  (let [name (concept->name concept)]
    {:name  name
     :value [(replace-placeholders value data)]
     :type  (get types name "Str")}))

(defmethod build-variable :dictionary-item [{value :value {item-name :name} :attributes :as concept} {:keys [types dictionary]}]
  (let [name (concept->name concept)
        dict-entry (get dictionary value)]
    {:name  name
     :value (-> dict-entry
                (cond->> (empty? dict-entry) (cons (or item-name value)))
                (distinct)
                (into []))
     :type  (get types name "Str")}))

(defmulti build-function (fn [concept _ _ _] (:type concept)))

(defmethod build-function :default [concept children _ _]
  {:name   (concept->name concept)
   :params (get-params children)
   :body   (for [child-concept children]
             {:kind  (get-kind child-concept)
              :value (concept->name child-concept)})
   :ret    [:s "Str"]})

(defmethod build-function :modifier [concept children relations _]
  (let [child-concept (get-child-with-role children relations :child)
        modifier-concepts (remove #(= (:id child-concept) (:id %)) children)]
    {:name   (concept->name concept)
     :params (get-params children)
     :body   (cond-> (map (fn [modifier-concept]
                            {:kind  (get-kind modifier-concept)
                             :value (concept->name modifier-concept)})
                          modifier-concepts)
                     (some? child-concept) (concat [{:kind  (get-kind child-concept)
                                                     :value (concept->name child-concept)}]))
     :ret    [:s "Str"]}))

(defmethod build-function :amr [{value :value :as concept} children relations {amr :amr}]
  (let [role-map (reduce (fn [m [{{attr-name :name} :attributes} concept]]
                           (cond-> m (some? attr-name) (assoc attr-name concept)))
                         {}
                         (zipmap relations children))]
    {:name   (concept->name concept)
     :params (get-params children)
     :body   (for [syntax (->> (get amr value) (:frames) (map :syntax))]
               (for [{:keys [value pos role params type] :as attrs} syntax]
                 (let [role-key (when (some? role) role)]
                   (-> (cond
                         (contains? role-map role-key) (let [role-concept (get role-map role-key)]
                                                         {:kind  (get-kind role-concept)
                                                          :value (concept->name role-concept)})
                         (= :oper type) {:kind   :operation
                                         :value  value
                                         :params (map (fn [{:keys [role]}]
                                                        (let [role-concept (get role-map role)]
                                                          {:kind  (get-kind role-concept)
                                                           :value (concept->name role-concept)}))
                                                      params)}
                         (some? role) {:kind  :literal
                                       :value (format "{{%s}}" role)}
                         (= pos :AUX) {:kind  :function
                                       :value "(copula Sg)"}
                         (some? value) {:kind  :literal
                                        :value value}
                         :else {:kind  :literal
                                :value "{{...}}"})
                       (attach-selectors attrs)
                       (cond-> (when (some? pos)) (assoc :pos pos))))))
     :ret    [:s "Str"]}))

(defmethod build-function :shuffle [concept children _ _]
  {:name   (concept->name concept)
   :params (get-params children)
   :body   (for [permutation (filter seq (permutations children))]
             (for [child-concept permutation]
               {:kind  (get-kind child-concept)
                :value (concept->name child-concept)}))
   :ret    [:s "Str"]})

(defmethod build-function :synonyms [concept children _ _]
  {:name   (concept->name concept)
   :params (get-params children)
   :body   (for [child-concept children]
             [{:kind  (get-kind child-concept)
               :value (concept->name child-concept)}])
   :ret    [:s "Str"]})

(defmethod build-function :reference [concept children _ _]
  {:name   (concept->name concept)
   :params (get-params children)
   :body   (for [child-concept children]
             [{:kind  (get-kind child-concept)
               :value (concept->name child-concept)}])
   :ret    [:s "Str"]})

(defn find-types [concept-map relation-map {amr :amr}]
  (reduce (fn [m {:keys [id value]}]
            (let [relations (get relation-map id)
                  children (map (comp concept-map :to) relations)
                  role-map (reduce (fn [m [{{attr-name :name} :attributes} concept]]
                                     (cond-> m
                                             (some? attr-name) (assoc attr-name (concept->name concept))))
                                   {}
                                   (zipmap relations children))
                  params (->> (get amr value) (:frames) (mapcat :syntax) (mapcat :params) (distinct))]
              (cond-> m
                      (seq params) (merge (zipmap
                                            (map (comp role-map :role) params)
                                            (map :type params))))))
          {}
          (filter #(= :amr (:type %)) (vals concept-map))))

(defn build-grammar [module instance {::sg/keys [concepts relations]} context]
  (let [concept-map (zipmap (map :id concepts) concepts)
        relation-map (group-by :from relations)
        context (assoc context :types (find-types concept-map relation-map context))
        {function-concepts :fn variable-concepts :var} (group-by (fn [{concept-type :type}]
                                                                   (if (contains? data-types concept-type) :var :fn))
                                                                 concepts)]
    #:acc-text.nlg.gf.grammar{:module    module
                              :instance  instance
                              :flags     {:startcat (concept->name (first concepts))}
                              :variables (map #(build-variable % context) variable-concepts)
                              :functions (map (fn [{id :id :as concept}]
                                                (let [relations (get relation-map id)
                                                      children (map (comp concept-map :to) relations)]
                                                  (build-function concept children relations context)))
                                              function-concepts)}))
