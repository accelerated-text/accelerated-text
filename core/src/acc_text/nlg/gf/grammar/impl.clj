(ns acc-text.nlg.gf.grammar.impl
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.tools.logging :as log]))

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

(defmethod build-variable :dictionary-item [{value :value {key :name} :attributes :as concept} {:keys [dictionary types]}]
  (let [name (concept->name concept)
        item (get dictionary key)
        variants (get dictionary value)
        body {:name name :type (get types name "Str")}]
    (cond
      (map? item) (assoc body :item item)
      (seq item) (assoc body :value item)
      (seq variants) (assoc body :value variants)
      :else (assoc body :value [value]))))

(defmulti build-function (fn [concept _ _ _ _] (:type concept)))

(defmethod build-function :default [concept children _ _ _]
  {:name   (concept->name concept)
   :type   (:type concept)
   :params (get-params children)
   :body   (for [child-concept children]
             {:kind  (get-kind child-concept)
              :value (concept->name child-concept)})
   :ret    [:s "Str"]})

(defmethod build-function :modifier [concept children relations _ {types :types}]
  (let [name (concept->name concept)
        child-concept (get-child-with-role children relations :child)
        modifier-concepts (remove #(= (:id child-concept) (:id %)) children)]
    {:name   name
     :type   :modifier
     :params (get-params children)
     :body   (cond-> (map (fn [modifier-concept]
                            {:kind  (get-kind modifier-concept)
                             :value (concept->name modifier-concept)})
                          modifier-concepts)
                     (some? child-concept) (concat [{:kind  (get-kind child-concept)
                                                     :value (concept->name child-concept)}]))
     :ret    [:s (get types name "Str")]}))

(defn syntax-suitable-for-role
  "Take the set of AMR syntax variants and return only those suitable for a given role"
  [role amr]
  (->> amr
       :frames
       (map :syntax)
       (filter (fn [[body-start & body]]
                 (if (and (= "Subject" role)
                          (nil? body))
                   (not (= "S" (:ret body-start)))
                   (or (not (nil? body))
                       (= "S" (:ret body-start))))))))

(defmethod build-function :amr
  [{value :value :as concept} children
   from-relations {{to-relation-role :name} :attributes} {amr :amr}]
  (let [role-map (reduce (fn [m [{{attr-name :name label :label} :attributes} concept]]
                           (cond
                             (some? label) (assoc m label concept)
                             (some? attr-name) (assoc m attr-name concept)
                             :else m))
                         {}
                         (zipmap from-relations children))]
    {:name   (concept->name concept)
     :type   :amr
     :params (get-params children)
     :body   (let [{:keys [id frames roles semantic-graph]} (get amr value)]
               (cond
                 (some? frames) (for [syntax (->> (get amr value) (syntax-suitable-for-role to-relation-role))]
                                  (for [{:keys [value pos role params type]} syntax]
                                    (let [role-key (when (some? role) role)]
                                      (-> (cond
                                            (contains? role-map role-key) (let [role-concept (get role-map role-key)]
                                                                            {:kind  (get-kind role-concept)
                                                                             :value (concept->name role-concept)})
                                            (= :oper type)
                                            {:kind   :operation
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
                                          #_(attach-selectors attrs)
                                          (cond-> (when (some? pos)) (assoc :pos pos))))))
                 (some? semantic-graph) (->> roles
                                             (map (comp #(hash-map :kind :variable :value %) concept->name role-map :label))
                                             (assoc {:kind :operation :value id} :params)
                                             (vector))))
     :ret    (if (= "Subject" to-relation-role) "NP" [:s "Str"])}))

(defmethod build-function :shuffle [concept children _ _ _]
  {:name   (concept->name concept)
   :type   :shuffle
   :params (get-params children)
   :body   (for [permutation (filter seq (permutations children))]
             (for [child-concept permutation]
               {:kind  (get-kind child-concept)
                :value (concept->name child-concept)}))
   :ret    [:s "Str"]})

(defmethod build-function :synonyms [concept children _ _ _]
  {:name   (concept->name concept)
   :type   :synonyms
   :params (get-params children)
   :body   (for [child-concept children]
             [{:kind  (get-kind child-concept)
               :value (concept->name child-concept)}])
   :ret    [:s "Str"]})

(defmethod build-function :reference [concept children _ _ _]
  {:name   (concept->name concept)
   :type   :reference
   :params (get-params children)
   :body   (for [child-concept children]
             [{:kind  (get-kind child-concept)
               :value (concept->name child-concept)}])
   :ret    [:s "Str"]})

(defn add-child-types [parent-types child-concepts concept-map relation-map]
  (->> child-concepts
       (remove #(contains? data-types (:type %)))
       (map (fn [{:keys [id type] :as concept}]
              (let [concept-name (concept->name concept)
                    relations (get relation-map id)
                    children (map (comp concept-map :to) relations)]
                (case type
                  :modifier (let [parent-type (get parent-types concept-name)
                                  child-concept (get-child-with-role children relations :child)
                                  modifier-concepts (remove #(= (:id child-concept) (:id %)) children)]
                              (case parent-type
                                "CN" (-> (map concept->name modifier-concepts)
                                         (zipmap (repeat "A"))
                                         (assoc (concept->name child-concept) "N"))
                                (log/errorf "Type inference not implemented for concept type `%s` with parent type `%s`"
                                            (name type) parent-type)))
                  (log/errorf "Type inference not implemented for concept type `%s`"
                              (name type))))))
       (apply merge parent-types)))

(defn find-types [concept-map relation-map {amr :amr}]
  (reduce (fn [m {:keys [id value]}]
            (let [relations (get relation-map id)
                  children (map (comp concept-map :to) relations)
                  role-map (reduce (fn [m [{{attr-name :name} :attributes} concept]]
                                     (cond-> m
                                             (some? attr-name) (assoc attr-name (concept->name concept))))
                                   {}
                                   (zipmap relations children))
                  params (or
                           (->> (get amr value) (:frames) (mapcat :syntax) (mapcat :params) (distinct) (seq))
                           (->> (get amr value) (:roles) (map #(set/rename-keys % {:label :role})) (seq)))]
              (cond-> m
                      (some? params) (merge (-> (when (every? :role params)
                                                  (zipmap
                                                    (map (comp role-map :role) params)
                                                    (map :type params)))
                                                (add-child-types children concept-map relation-map))))))
          {}
          (filter #(= :amr (:type %)) (vals concept-map))))

(defn get-children [id concept-map relation-map]
  (map (comp concept-map :to)
       (get relation-map id)))

(defmulti build-operation (fn [concept _ _ _ _] (:type concept)))

(defmethod build-operation :document-plan [concept concept-map relation-map role-map rgl-ops]
  (interpose {:type  :operator
              :value "|"}
             (map #(build-operation % concept-map relation-map role-map rgl-ops)
                  (get-children (:id concept) concept-map relation-map))))

(defmethod build-operation :segment [concept concept-map relation-map role-map rgl-ops]
  (interpose {:type  :operator
              :value "|"}
             (map #(build-operation % concept-map relation-map role-map rgl-ops)
                  (get-children (:id concept) concept-map relation-map))))

(defmethod build-operation :amr [concept concept-map relation-map role-map rgl-ops]
  (let [{:keys [module label kind]} (get rgl-ops (:value concept))]
    {:type     :operation
     :value    (cond
                 (and (some? module) (some? label)) (str module "." label)
                 (some? label) label
                 :else (:value concept))
     :kind     (or kind "Text")
     :children (map #(build-operation % concept-map relation-map role-map rgl-ops)
                    (get-children (:id concept) concept-map relation-map))}))

(defmethod build-operation :condition [concept concept-map relation-map role-map rgl-ops]
  (map #(build-operation % concept-map relation-map role-map rgl-ops)
       (get-children (:id concept) concept-map relation-map)))

(defmethod build-operation :if-statement [concept concept-map relation-map role-map rgl-ops]
  (map #(build-operation % concept-map relation-map role-map rgl-ops)
       (get-children (:id concept) concept-map relation-map)))

(defmethod build-operation :reference [{{name :name} :attributes} _ _ role-map _]
  (let [{:keys [type id]} (get role-map name)]
    {:value id
     :kind  type
     :type  :argument}))

(defmethod build-operation :quote [{value :value} _ _ _ _]
  {:type  :literal
   :value value})

(defn build-operations [{amr :amr}]
  (let [{:keys [rgl-ops amr-ops]} (group-by #(if (contains? % :semantic-graph) :amr-ops :rgl-ops) (vals amr))
        rgl-ops (zipmap (map :id rgl-ops) rgl-ops)]
    (for [{name :id roles :roles {::sg/keys [concepts relations]} :semantic-graph} amr-ops]
      (let [concept-map (zipmap (map :id concepts) concepts)
            relation-map (group-by :from relations)
            role-map (zipmap (map :label roles) roles)
            root-concept (first concepts)]
        {:id    name
         :roles roles
         :kind  "Text"
         :body  (flatten (build-operation root-concept concept-map relation-map role-map rgl-ops))}))))

(defn build-grammar [module instance {::sg/keys [concepts relations]} context]
  (let [concept-map (zipmap (map :id concepts) concepts)
        from-relation-map (group-by :from relations)
        to-relation-map (group-by :to relations)
        context (assoc context :types
                               (find-types concept-map from-relation-map context))
        {function-concepts :fn variable-concepts :var}
        (group-by (fn [{concept-type :type}]
                    (if (contains? data-types concept-type) :var :fn))
                  concepts)]
    #:acc-text.nlg.gf.grammar
        {:module     module
         :instance   instance
         :flags      {:startcat (concept->name (first concepts))}
         :variables  (map #(build-variable % context) variable-concepts)
         :operations (build-operations context)
         :functions  (doall
                       (map (fn [{id :id :as concept}]
                              (let [from-relations (get from-relation-map id)
                                    to-relation (first (get to-relation-map id))
                                    children (map (comp concept-map :to) from-relations)]
                                (build-function concept children
                                                from-relations to-relation
                                                context)))
                            function-concepts))}))
