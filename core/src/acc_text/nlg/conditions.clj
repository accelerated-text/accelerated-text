(ns acc-text.nlg.conditions
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))

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
  (let [operator-fn (operator->fn operator)
        normalized-args (normalize args)]
    (when (and
            (seq args)
            (or
              (contains? #{"=" "!="} operator)
              (and
                (contains? #{"<" "<=" ">" ">="} operator)
                (every? number? normalized-args))))
      (apply operator-fn normalized-args))))

(defn evaluate-conditions [{::sg/keys [concepts relations]} data]
  (let [concept-map (zipmap (map ::sg/id concepts) concepts)
        relation-map (group-by ::sg/from relations)]
    (reduce (fn [m {::sg/keys [from to]}]
              (let [comparator (get concept-map to)
                    comparables (map #(get concept-map (::sg/to %))
                                     (get relation-map to))]
                (assoc m from (comparison
                                (get comparator ::sg/value)
                                (for [{::sg/keys [type value]} comparables]
                                  (case type
                                    :quote value
                                    :data (get data value)
                                    nil))))))
            {}
            (filter #(= :predicate (::sg/role %)) relations))))
