(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :as gut]
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
  (uber/remove-nodes g node))

(defn gf-parent-operations [g list-node]
  (filter #(= :operation (:type (uber/attrs g %)))
          (gut/get-predecessors g list-node)))

(defn rearange-list-connections [g list-node gf-funct data-leaf]
  (let [new-id (UUID/randomUUID) new-attrs (uber/attrs g gf-funct)]
    (-> g
        ;;add a new node (clone) for gf operation, old one will be removed
        (add-node new-id new-attrs)
        ;;connect new operation ode with data (Str)
        (add-edge new-id data-leaf (attrs-from-edge g gf-funct list-node))
        ;;connect list and new operation nodes
        (add-edge list-node new-id (attrs-from-edge g list-node data-leaf))
        ;;drop old connections which went from list directly to Str
        (remove-edge list-node data-leaf))))

(defn construct-list-structure [g list-node gf-funct]
  (let [list-parent (first (gut/get-predecessors g gf-funct))
        g-with-new-list-edges
        (-> g
            (add-edge list-parent list-node (attrs-from-edge g list-parent gf-funct))
            (remove-edge list-parent gf-funct))]
    (-> (reduce ;;go over children of list node and connect with parent
         (fn [g child] (rearange-list-connections g list-node gf-funct child))
         g-with-new-list-edges
         (gut/get-successors g list-node))
        (remove-edge gf-funct list-node)
        (remove-node gf-funct))))

(defn find-list-nodes [g]
  (concat (gut/find-nodes g {:type :synonyms}) (gut/find-nodes g {:type :shuffle})))

(defn resolve-lists [g]
  (reduce ;;first, iterate over all list nodes
   (fn [g [list-node _]]
     (reduce ;;then iterate over parents of each list node
      (fn [g parent]
        (log/debugf "Rearranging list %s with parent %s" list-node parent)
        (construct-list-structure g list-node parent))
      g (gf-parent-operations g list-node)))
   g (find-list-nodes g)))
