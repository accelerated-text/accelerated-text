(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :as gut :refer [find-nodes get-in-edge add-edges]]
            [clojure.tools.logging :as log]
            [loom.attr :as attr]
            [loom.graph :as graph])
  (:import java.util.UUID))

(defn clone-node [g new-id source]
  (graph/add-nodes g [^:node new-id (attr/attrs g source)]))

(defn resolve-lists [g]
  ;;i (UUID/randomUUID)
  (reduce
   (fn [g [list-node-id _]]
     (reduce (fn [g parent]
               (reduce (fn [g child]
                         (log/debugf "Pred: %s" (attrs g parent))
                         (log/debugf "Succ: %s" (attrs g child))
                         (if (= :operation (:type (attrs g parent)))
                           (graph/add-edges g [^:edge parent child {:X 110000}])
                           g))
                       g (gut/get-successors g list-node-id)))
             g (gut/get-predecessors g list-node-id)))
   g (concat (find-nodes g {:type :synonyms}) (find-nodes g {:type :shuffle}))))
