(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :as gut :refer [find-nodes]]
            [clojure.tools.logging :as log]
            [ubergraph.core :as uber]
            [loom.attr :as attr]
            [loom.graph :as graph])
  (:import java.util.UUID))

(defn add-node [g node attrs]
  (uber/add-nodes-with-attrs g [node attrs]))

(defn resolve-lists [g]
  (reduce ;;first, iterate over all list nodes
   (fn [g [list-node-id _]]
     (reduce ;;then iterate over parents of each a list node
      (fn [g parent]
        (reduce ;;lastly go over children of list node and connect with parent
         (fn [g child]
           (log/debugf "Pred: %s" (attr/attrs g parent))
           (log/debugf "Succ: %s" (attr/attrs g child))
           (if (= :operation (:type (attr/attrs g parent)))
             (let [new-id (UUID/randomUUID)
                   new-attrs (attr/attrs g parent)]
               (-> g
                   (add-node new-id new-attrs)
                   (graph/add-edges
                    [^:edge new-id child {:X 22}])))
             g))
         g (gut/get-successors g list-node-id)))
      g (gut/get-predecessors g list-node-id)))
   g (concat (find-nodes g {:type :synonyms}) (find-nodes g {:type :shuffle}))))
