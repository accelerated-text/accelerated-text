(ns acc-text.nlg.semantic-graph.conditions
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn operator->fn [x]
  (case x
    "=" =
    "!=" not=
    "<" <
    "<=" <=
    ">" >
    ">=" >=
    nil))

(defn normalize [xs]
  (->> xs
       (map #(cond-> % (string? %) (str/trim)))
       (map #(cond-> % (try
                         (bigdec %)
                         (catch Exception _)) (bigdec)))))

(defn comparison [operator args]
  (let [operator-fn (operator->fn (log/spy operator))
        normalized-args (normalize (log/spy args))]
    (when (and
            (seq args)
            (or
              (contains? #{"=" "!="} operator)
              (and
                (contains? #{"<" "<=" ">" ">="} operator)
                (every? number? normalized-args))))
      (apply operator-fn normalized-args))))

(defn evaluate-predicate [comparator-concept value-concepts data]
  (when (every? #(contains? #{:data :quote} (::sg/type %)) (log/spy value-concepts))
    (comparison
      (get comparator-concept ::sg/value)
      (for [{::sg/keys [type value]} value-concepts]
        (case type
          :quote value
          :data (get data (keyword value)))))))

(defn evaluate-statement [{::sg/keys [id type]} concept-map relation-map data]
  (case type
    :if-statement (let [predicate (some #(when (= :predicate (::sg/role %)) %) (get relation-map id))]
                    (when (some? predicate)
                      (evaluate-predicate (get concept-map (::sg/to predicate))
                                          (map #(get concept-map (::sg/to %))
                                               (get relation-map (::sg/to predicate)))
                                          data)))
    :default-statement true
    nil))

(defn evaluate-conditions [{::sg/keys [concepts relations] :as semantic-graph} data]
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

(defn find-statement-ids [semantic-graph]
  (sg-utils/find-child-ids semantic-graph (sg-utils/find-concept-ids semantic-graph :condition)))

(defn select [{concepts ::sg/concepts :as semantic-graph} data]
  (->> (evaluate-conditions semantic-graph data)
       (vals)
       (remove nil?)
       (set/difference (find-statement-ids semantic-graph))
       (set/union (sg-utils/find-concept-ids semantic-graph :comparator))
       (sg-utils/prune-branches semantic-graph)))
