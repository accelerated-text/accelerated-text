(ns acc-text.nlg.graph.variables
  (:require [acc-text.nlg.graph.utils :refer [find-nodes]]
            [loom.graph :as graph]))

(defn resolve-variables [g]
  (reduce (fn [g [node-id _]]
            (cond-> g (zero? (graph/out-degree g node-id)) (graph/remove-nodes g node-id)))
          g
          (find-nodes g {:type :reference})))
