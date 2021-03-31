(ns acc-text.nlg.graph.condition
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-successors add-edges remove-edges]]
            [clojure.string :as str]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]))

(defn string->boolean [s]
  (when (some? s)
    (case (-> s (str/lower-case) (str/trim))
      "" false
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
    "==" =
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
  (letfn [(extract-number [s] (re-find #"[-\d.]+" s))]
    (sequence
      (comp
        (map #(cond-> % (string? %) (str/trim)))
        (map #(cond-> % (string? %) (-> (extract-number) (or %))))
        (map #(cond-> % (try
                          (bigdec %)
                          (catch Exception _)) (bigdec))))
      xs)))

(defn comparison [operator args]
  (let [operator-fn (operator->fn operator)
        normalized-args (normalize args)]
    (when (and (< 1 (count args)) (or (contains? #{"=" "==" "!=" "in"} operator)
                                      (and (contains? #{"<" "<=" ">" ">="} operator)
                                           (every? number? normalized-args))))
      (apply operator-fn normalized-args))))

(defmulti evaluate-predicate (fn [g node-id _] (:type (attrs g node-id))))

(defmethod evaluate-predicate :default [_ _ _] true)

(defmethod evaluate-predicate :comparator [g node-id {:keys [data constants]}]
  (let [operator (:value (attrs g node-id))
        successors (get-successors g node-id)]
    (when (every? #(contains? #{:data :quote :constant} (:type (attrs g %))) successors)
      (comparison operator (for [{:keys [type value name]} (map #(attrs g %) successors)]
                             (case type
                               :quote value
                               :data (get data name)
                               :constant (get constants name)))))))

(defmethod evaluate-predicate :boolean [g node-id context]
  (let [operator-fn (operator->fn (:value (attrs g node-id)))
        successors (get-successors g node-id)]
    (when (every? #(contains? #{:boolean :comparator :data :quote} (:type (attrs g %))) successors)
      (operator-fn (map #(evaluate-predicate g % context) successors)))))

(defmethod evaluate-predicate :data [g node-id {data :data}]
  (string->boolean (get data (:name (attrs g node-id)))))

(defmethod evaluate-predicate :quote [g node-id _]
  (string->boolean (:value (attrs g node-id))))

(defn evaluate-statement [g node-id context]
  (case (:type (attrs g node-id))
    :if-statement (when-let [predicate-node (some #(when (= :predicate (:role (attrs g %))) (graph/dest %))
                                                  (graph/out-edges g node-id))]
                    (evaluate-predicate g predicate-node context))
    :else-statement true
    nil))

(defn find-truthful-edges [g condition-node-id context]
  (->> condition-node-id
       (get-successors g)
       (some #(when (true? (evaluate-statement g % context)) %))
       (graph/out-edges g)
       (remove #(= :predicate (:role (attrs g %))))))

(defn determine-conditions [g context]
  (reduce (fn [g [node-id _]]
            (let [in-edges (graph/in-edges g node-id)
                  truthful-edges (find-truthful-edges g node-id context)]
              (cond-> (remove-edges g in-edges)
                      (some? truthful-edges) (add-edges
                                               (for [in-edge in-edges
                                                     out-edge truthful-edges]
                                                 [^:edge (graph/src in-edge) (graph/dest out-edge) (attrs g in-edge)])))))
          g
          (find-nodes g {:type :condition})))
