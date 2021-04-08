(ns acc-text.nlg.graph.dictionary
  (:require [acc-text.nlg.graph.utils :refer [find-nodes]]))

(defn ensure-dictionary-items [g {dictionary :dictionary}]
  (doseq [[_ {key :label category :category}] (find-nodes g {:type :dictionary-item})]
    (when-not (contains? dictionary [key category])
      (throw (Exception. (format "Dictionary item `%s` (%s) not found." key category)))))
  g)
