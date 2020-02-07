(ns acc-text.nlg.utils.ref-expressions
  (:require [acc-text.nlg.utils.nlp :as nlp]))

(defn filter-by-refs-count
  [[k refs]]
  (>= (count refs) 2))

(defn identify-potential-refs
  [tokens]
  (->> (map-indexed vector tokens)
       (filter #(nlp/starts-with-capital? (second %)))
       (group-by second)
       (vec)
       (filter filter-by-refs-count)
       (map second)))
