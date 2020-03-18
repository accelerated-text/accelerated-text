(ns acc-text.nlg.graph.variables
  (:require [acc-text.nlg.graph.utils :refer [find-nodes]]
            [loom.graph :as graph]))

(defn resolve-variables [g]
  (reduce (fn [g [node-id _]]
            (let [out-edges (graph/out-edges g node-id)]
              (cond-> g
                      (empty? out-edges) (graph/remove-nodes node-id))))
          g
          (find-nodes g {:type :reference})))
