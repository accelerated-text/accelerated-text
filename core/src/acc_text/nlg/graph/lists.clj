(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-in-edge]]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]))

(defn resolve-lists [g]
  (reduce (fn [g [node-id _]]
            (let [{category :category :as in-edge-attrs} (attrs g (get-in-edge g node-id))]
              (reduce (fn [g edge]
                        (assoc-in g [:attrs (:id edge)] in-edge-attrs))
                      (cond-> g
                              (some? category) (update-in [:attrs node-id] #(assoc % :category category)))
                      (graph/out-edges g node-id))))
          g
          (find-nodes g {:type :synonyms})))
