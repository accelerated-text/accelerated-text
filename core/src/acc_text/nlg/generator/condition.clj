(ns acc-text.nlg.generator.condition
  (:require [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.string :as str]))

(defn string->boolean [s]
  (when-not (str/blank? s)
    (case (-> s (str/lower-case) (str/trim))
      "true" true
      "false" false
      "yes" true
      "no" false
      "1" true
      "0" false
      true)))

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

(defn normalize [x]
  (cond-> x
          (string? x) (str/trim)
          (try
            (bigdec x)
            (catch Exception _)) (bigdec)))

(defn comparison [operator args]
  (let [operator-fn (operator->fn operator)
        normalized-args (map normalize args)]
    (when (and (< 1 (count args)) (or (contains? #{"=" "!=" "in"} operator)
                                      (and (contains? #{"<" "<=" ">" ">="} operator)
                                           (every? number? normalized-args))))
      (apply operator-fn normalized-args))))

(defmulti evaluate-predicate (fn [concept _ _] (:type concept)))

(defmethod evaluate-predicate :comparator [{operator :value :as concept} semantic-graph data]
  (let [child-concepts (sg-utils/get-children semantic-graph concept)]
    (when (every? #(contains? #{:data :quote :constant} (:type %)) child-concepts)
      (comparison operator (for [{:keys [type value attributes]} child-concepts]
                             (case type
                               :quote value
                               :data (get data (keyword value))
                               :constant (case (:name attributes)
                                           "*Language" (get data :lang))))))))

(defmethod evaluate-predicate :boolean [{operator :value :as concept} semantic-graph data]
  (let [child-concepts (sg-utils/get-children semantic-graph concept)
        operator-fn (operator->fn operator)]
    (when (every? #(contains? #{:boolean :comparator} (:type %)) child-concepts)
      (operator-fn (map #(evaluate-predicate % semantic-graph data) child-concepts)))))

(defmethod evaluate-predicate :data [{value :value} _ data]
  (-> data
      (get value)
      (string->boolean)))

(defn evaluate-statement [{type :type :as concept} semantic-graph data]
  (case type
    :if-statement (when-let [predicate-concept (sg-utils/get-child-with-relation semantic-graph concept :predicate)]
                    (evaluate-predicate predicate-concept semantic-graph data))
    :default-statement true
    nil))
