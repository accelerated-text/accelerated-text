(ns acc-text.nlg.graph.paths
  (:require [acc-text.nlg.gf.paths :refer [path-map]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [loom.alg :as alg]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [ubergraph.core :as uber]))

(defn join-path [g path-g in-edge out-attrs]
  (let [sorted-nodes (alg/bf-traverse path-g)
        start-node (first sorted-nodes)
        end-node (last sorted-nodes)]
    (-> (uber/build-graph g path-g)
        (graph/remove-edges in-edge)
        (graph/add-edges
          [^:edge (graph/src in-edge) start-node (attrs g in-edge)]
          [^:edge end-node (graph/dest in-edge) out-attrs]))))

(defn resolve-paths [g {{language "*Language"} :constants}]
  (reduce (fn [g node-id]
            (reduce (fn [g in-edge]
                      (let [category (get (attrs g node-id) :category "Str")
                            in-edge-category (get (attrs g in-edge) :category "Text")
                            path-sg (get-in path-map [language [category in-edge-category]])]
                        (cond-> g
                                (and
                                  (not= category in-edge-category)
                                  (some? path-sg))
                                (join-path
                                  (semantic-graph->ubergraph path-sg)
                                  in-edge
                                  {:role :arg :index 0 :category category}))))
                    g
                    (graph/in-edges g node-id)))
          g
          (filter #(contains? #{:operation :dictionary-item :quote} (:type (attrs g %))) (graph/nodes g))))
