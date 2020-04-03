(ns acc-text.nlg.graph.variables
  (:require [acc-text.nlg.graph.utils :refer [add-edges find-nodes get-successors]]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]))

(defn resolve-variables [g]
  (reduce (fn [g [node-id _]]
            (let [in-edges (graph/in-edges g node-id)
                  out-edges (graph/out-edges g node-id)]
              (cond-> (graph/remove-nodes g node-id)
                      (some? out-edges) (add-edges
                                          (for [in-edge in-edges
                                                out-edge (mapcat #(graph/out-edges g %) (get-successors g node-id))]
                                            [^:edge (graph/src in-edge) (graph/dest out-edge) (attrs g in-edge)])))))
          g
          (find-nodes g {:type :reference})))
