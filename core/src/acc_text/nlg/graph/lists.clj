(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-in-edge get-successors]]
            [clojure.math.combinatorics :refer [permutations]]
            [loom.graph :as graph]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]))

(defn resolve-synonyms [g]
  (reduce (fn [g [node-id _]]
            (let [{category :category :as in-edge-attrs} (attrs g (get-in-edge g node-id))]
              (reduce (fn [g edge]
                        (assoc-in g [:attrs (:id edge)] in-edge-attrs))
                      (cond-> g
                        (some? category) (update-in [:attrs node-id] #(assoc % :category category)))
                      (graph/out-edges g node-id))))
          g
          (find-nodes g {:type :synonyms})))


(defn resolve-shuffle [g]
  (reduce (fn [g [node-id _]]
            (let [edges (get-successors g node-id)
                  variants (permutations edges)]
              (update-in g [:attrs node-id] #(assoc % :children variants))))
          g
          (find-nodes g {:type :shuffle})))

(defn resolve-lists [g]
  (-> g
      (resolve-synonyms)
      (resolve-shuffle)))
