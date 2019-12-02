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
    "not" (fn [[arg]] (when (boolean? arg) (not arg)))
    "xor" (fn [args] (odd? (count (filter true? args))))))

(defn normalize [xs]
  (->> xs
       (map #(cond-> % (string? %) (str/trim)))
       (map #(cond-> % (try
                         (bigdec %)
                         (catch Exception _)) (bigdec)))))

(defn comparison [operator args]
  (let [operator-fn (operator->fn operator)
        normalized-args (normalize args)]
    (when (and (< 1 (count args)) (or (contains? #{"=" "!=" "in"} operator)
                                      (and (contains? #{"<" "<=" ">" ">="} operator)
                                           (every? number? normalized-args))))
      (apply operator-fn normalized-args))))

(defmulti evaluate-predicate (fn [concept _ _] (::sg/type concept)))

(defmethod evaluate-predicate :comparator [{operator ::sg/value :as concept} semantic-graph data]
  (let [child-concepts (sg-utils/get-children semantic-graph concept)]
    (when (every? #(contains? #{:data :quote} (::sg/type %)) child-concepts)
      (comparison operator (for [{::sg/keys [type value]} child-concepts]
                             (case type
                               :quote value
                               :data (get data (keyword value))))))))

(defmethod evaluate-predicate :boolean [{operator ::sg/value :as concept} semantic-graph data]
  (let [child-concepts (sg-utils/get-children semantic-graph concept)
        operator-fn (operator->fn operator)]
    (when (every? #(contains? #{:boolean :comparator} (::sg/type %)) child-concepts)
      (operator-fn (map #(evaluate-predicate % semantic-graph data) child-concepts)))))

(defn evaluate-statement [{type ::sg/type :as concept} semantic-graph data]
  (case type
    :if-statement (when-let [predicate-concept (sg-utils/get-child-with-relation semantic-graph concept :predicate)]
                    (evaluate-predicate predicate-concept semantic-graph data))
    :default-statement true
    nil))

(defn evaluate-conditions [semantic-graph data]
  (reduce (fn [m {id ::sg/id :as condition-concept}]
            (assoc m id (->> (some (fn [{id ::sg/id :as statement-concept}]
                                     (when (true? (evaluate-statement statement-concept semantic-graph data))
                                       id))
                                   (sg-utils/get-children semantic-graph condition-concept)))))
          {}
          (sg-utils/get-concepts-with-type semantic-graph :condition)))

(defn get-truthful-statement-ids [semantic-graph data]
  (->> (evaluate-conditions semantic-graph data)
       (vals)
       (remove nil?)
       (into #{})))

(defn find-statement-ids [semantic-graph]
  (->> #{:condition}
       (sg-utils/find-concept-ids semantic-graph)
       (sg-utils/find-child-ids semantic-graph)))

(defn select [semantic-graph data]
  (->> (get-truthful-statement-ids semantic-graph data)
       (set/difference (find-statement-ids semantic-graph))
       (set/union (sg-utils/find-concept-ids semantic-graph #{:boolean :comparator}))
       (sg-utils/prune-branches semantic-graph)))
