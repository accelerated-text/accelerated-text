(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :as gut :refer [find-nodes get-in-edge add-edges]]
            [clojure.tools.logging :as log]
            [loom.attr :as attr]
            [loom.graph :as graph])
  (:import java.util.UUID))

(defn clone-node [g new-id source]
  (graph/add-nodes g [^:node new-id (attr/attrs source)]))

(defn resolve-lists [g]
  (reduce (fn [g [node-id _]]
            (let [{category :category :as in-edge-attrs} (attrs g (get-in-edge g node-id))
                  i (UUID/randomUUID)]

              (log/debugf "node: %s" node-id)

              (doseq [parent (gut/get-predecessors g node-id)]
                (log/debugf "Pred: %s" (attrs g parent))
                (if (= :operation (:type parent))
                  (graph/add-edges g [src dest (get-in amr-g [:attrs id])])
                  ))
              (doseq [n (gut/get-successors g node-id)] (log/debugf "Succ: %s" (attrs g n)))

              g


              #_(reduce (fn [g edge]
                        (assoc-in g [:attrs (:id edge)] in-edge-attrs))
                      (cond-> g
                        (some? category) (update-in [:attrs node-id]
                                                    #(assoc % :category category)))
                      (graph/out-edges g node-id))))
          g
          (concat (find-nodes g {:type :synonyms}) (find-nodes g {:type :shuffle}))))
