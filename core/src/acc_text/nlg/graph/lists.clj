(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :as gut :refer [find-nodes]]
            [clojure.tools.logging :as log]
            [ubergraph.core :as uber]
            [loom.graph :as graph])
  (:import java.util.UUID))

(defn add-node [g node attrs]
  (uber/add-nodes-with-attrs g [node attrs]))

(defn attrs-from-edge [g from to]
  (uber/attrs g (uber/find-edge g from to)))

(defn add-edge [g from to attrs]
  (graph/add-edges g [^:edge from to attrs]))

(defn remove-edge [g from to]
  (if-let [edge (uber/find-edge g from to)]
    (uber/remove-edges g edge) g))

(defn remove-node [g node]
  (prn (uber/attrs g node))
  (uber/remove-nodes g node))

(defn construct-list-structure [g list-node gf-funct data-leaf]
  (let [new-id (UUID/randomUUID) new-attrs (uber/attrs g gf-funct)]
    (-> g
        (add-node new-id new-attrs)
        (add-edge new-id data-leaf (attrs-from-edge g gf-funct list-node))
        (add-edge list-node new-id (attrs-from-edge g list-node data-leaf))
        (remove-edge list-node data-leaf))))

(defn resolve-lists [g]
  (reduce ;;first, iterate over all list nodes
   (fn [g [list-node _]]
     (reduce ;;then iterate over parents of each list node
      (fn [g parent]
        (let [list-parent (first (gut/get-predecessors g parent))]
          (remove-node
           (remove-edge
            (reduce ;;lastly go over children of list node and connect with parent
             (fn [g child]
               (if (= :operation (:type (uber/attrs g parent)))
                 (construct-list-structure g list-node parent child)
                 g))
             (-> g
                 (add-edge list-parent list-node (attrs-from-edge g list-parent parent))
                 (remove-edge list-parent parent))
             (gut/get-successors g list-node))
            parent list-node)
           parent)))
      g (gut/get-predecessors g list-node)))
   g (concat (find-nodes g {:type :synonyms}) (find-nodes g {:type :shuffle}))))
