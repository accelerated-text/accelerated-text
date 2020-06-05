(ns acc-text.nlg.graph.paths
  (:require [acc-text.nlg.gf.paths :refer [path-map]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [loom.alg :as alg]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [ubergraph.core :as uber]))

(defn join-inner-path [g path-g in-edge category]
  (let [sorted-nodes (alg/bf-traverse path-g)
        start-node (first sorted-nodes)
        end-node (last sorted-nodes)]
    (-> (uber/build-graph g path-g)
        (graph/remove-edges in-edge)
        (graph/add-edges
          [^:edge (graph/src in-edge) start-node (attrs g in-edge)]
          [^:edge end-node (graph/dest in-edge) {:role :arg :index 0 :category category}]))))

(defn resolve-inner-paths [g node lang]
  (reduce (fn [g in-edge]
            (let [category (get (attrs g node) :category "Str")
                  in-edge-category (get (attrs g in-edge) :category "Utt")
                  path-sg (get-in path-map [lang [category in-edge-category]])]
              (cond-> g
                      (and
                        (not= category in-edge-category)
                        (some? path-sg))
                      (join-inner-path
                        (semantic-graph->ubergraph path-sg)
                        in-edge
                        category))))
          g
          (graph/in-edges g node)))

(defn join-outer-path [g path-g out-edge out-category]
  (let [sorted-nodes (alg/bf-traverse path-g)
        start-node (first sorted-nodes)
        end-node (last sorted-nodes)]
    (-> (uber/build-graph g path-g)
        (graph/remove-edges out-edge)
        (graph/add-edges
          [^:edge (graph/src out-edge) start-node (assoc (attrs g out-edge) :category (:category (attrs path-g start-node)))]
          [^:edge end-node (graph/dest out-edge) {:role :arg :index 0 :category out-category}]))))

(defn resolve-outer-paths [g node lang]
  (reduce (fn [g out-edge]
            (let [category (get (attrs g node) :category "Utt")
                  out-edge-category (get (attrs g out-edge) :category "Str")
                  path-sg (get-in path-map [lang [out-edge-category category]])]
              (cond-> g
                      (and
                        (not= category out-edge-category)
                        (not= :operation (:type (attrs g node)))
                        (some? path-sg))
                      (join-outer-path
                        (semantic-graph->ubergraph path-sg)
                        out-edge
                        out-edge-category))))
          g
          (graph/out-edges g node)))

(defn resolve-paths [g {{language "*Language"} :constants}]
  (reduce (fn [g node]
            (-> g
                (resolve-inner-paths node language)
                (resolve-outer-paths node language)))
          g
          (graph/nodes g)))
