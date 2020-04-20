(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-in-edge] :as gut]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [clojure.tools.logging :as log]
            [ubergraph.core :as uber]))

(defn resolve-lists [g]
  (reduce (fn [g [node-id _]]
            (let [{category :category :as in-edge-attrs} (attrs g (get-in-edge g node-id))]
              (log/debugf "node: %s" node-id)
              (doseq [n (gut/get-predecessors g node-id)] (log/debugf "Pred: %s" (attrs g n)))
              (doseq [n (gut/get-successors g node-id)] (log/debugf "Succ: %s" (attrs g n)))
              (reduce (fn [g edge]
                        (assoc-in g [:attrs (:id edge)] in-edge-attrs))
                      (cond-> g
                        (some? category) (update-in [:attrs node-id]
                                                    #(assoc % :category category)))
                      (graph/out-edges g node-id))))
          g
          (concat (find-nodes g {:type :synonyms}) (find-nodes g {:type :shuffle}))))
