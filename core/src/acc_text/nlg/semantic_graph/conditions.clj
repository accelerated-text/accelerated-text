(ns acc-text.nlg.semantic-graph.conditions
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.set :as set]
            [clojure.string :as str]))

(defn operator->fn [x]
  (case x
    "=" =
    "!=" not=
    "<" <
    "<=" <=
    ">" >
    ">=" >=
    "in" (fn [s subs] (str/includes? (str s) (str subs)))
    "and" (fn [args] (every? true? args))
    "or" (fn [args] (some true? args))
    "not" (fn [[arg]] (not arg))
    "xor" (fn [args] (odd? (count (filter true? args))))
    nil))

(defn normalize [xs]
  (->> xs
       (map #(cond-> % (string? %) (str/trim)))
       (map #(cond-> % (try
                         (bigdec %)
                         (catch Exception _)) (bigdec)))))

(defn comparison [operator args]
  (let [operator-fn (operator->fn operator)
        normalized-args (normalize args)]
    (when (and
            (seq args)
            (or
              (contains? #{"=" "!=" "in"} operator)
              (and
                (contains? #{"<" "<=" ">" ">="} operator)
                (every? number? normalized-args))))
      (apply operator-fn normalized-args))))

(defmulti evaluate-predicate (fn [concept _ _ _] (::sg/type concept)))

(defmethod evaluate-predicate :comparator [{::sg/keys [id value]} concept-map relation-map data]
  (let [child-concepts (map #(get concept-map (::sg/to %)) (get relation-map id))]
    (when (every? #(contains? #{:data :quote} (::sg/type %)) child-concepts)
      (comparison value (for [{::sg/keys [type value]} child-concepts]
                          (case type
                            :quote value
                            :data (get data (keyword value))))))))

(defmethod evaluate-predicate :boolean [{::sg/keys [id value]} concept-map relation-map data]
  (let [child-concepts (map #(get concept-map (::sg/to %)) (get relation-map id))
        operator-fn (operator->fn value)]
    (when (every? #(contains? #{:boolean :comparator} (::sg/type %)) child-concepts)
      (operator-fn (map #(evaluate-predicate % concept-map relation-map data) child-concepts)))))

(defn get-predicate [{id ::sg/id} concept-map relation-map]
  (->> (get relation-map id)
       (some #(when (= :predicate (::sg/role %)) %))
       (::sg/to)
       (get concept-map)))

(defn evaluate-statement [{type ::sg/type :as concept} concept-map relation-map data]
  (case type
    :if-statement (when-let [predicate (get-predicate concept concept-map relation-map)]
                    (evaluate-predicate predicate concept-map relation-map data))
    :default-statement true
    nil))

(defn evaluate-conditions [{::sg/keys [concepts relations]} data]
  (let [concept-map (zipmap (map ::sg/id concepts) concepts)
        relation-map (group-by ::sg/from relations)]
    (reduce (fn [m {id ::sg/id}]
              (assoc m id (->> (get relation-map id)
                               (map (comp concept-map ::sg/to))
                               (some (fn [{id ::sg/id :as statement}]
                                       (when (true? (evaluate-statement statement concept-map relation-map data))
                                         id))))))
            {}
            (filter #(= :condition (::sg/type %)) concepts))))

(defn get-truthful-statement-ids [semantic-graph data]
  (->> (evaluate-conditions semantic-graph data)
       (vals)
       (remove nil?)
       (into #{})))

(defn find-statement-ids [semantic-graph]
  (sg-utils/find-child-ids semantic-graph (sg-utils/find-concept-ids semantic-graph #{:condition})))

(defn select [semantic-graph data]
  (->> (get-truthful-statement-ids semantic-graph data)
       (set/difference (find-statement-ids semantic-graph))
       (set/union (sg-utils/find-concept-ids semantic-graph #{:boolean :comparator}))
       (sg-utils/prune-branches semantic-graph)))
