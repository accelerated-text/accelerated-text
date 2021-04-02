(ns acc-text.nlg.graph.dictionary
  (:require [acc-text.nlg.graph.utils :refer [find-nodes]]
            [clojure.tools.logging :as log]))

(defn ensure-dictionary-items [g {dictionary :dictionary}]
  (doseq [[_ {key :label category :category :as attrs}] (find-nodes g {:type :dictionary-item})]
    (when-not (contains? dictionary [key category])
      (throw (Exception. (format "Dictionary item `%s` (%s) not found." key category)))))
  g)
