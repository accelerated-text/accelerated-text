(ns acc-text.nlg.graph.modifier
  (:require [acc-text.nlg.graph.utils :refer [find-nodes get-in-edge]]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [loom.alg :as alg]))

(defn resolve-modifier [g modifier-id]
  (reduce (fn [g [node-id _]]
            (let [successors (into #{} (alg/bf-traverse g node-id))]
              (cond-> g
                      (contains? successors modifier-id) (graph/add-edges
                                                           [^:edge node-id modifier-id {:role     :arg
                                                                                        :index    -1
                                                                                        :category "A"}]))))
          g
          (find-nodes g {:type :operation :name "mkCN"})))

(defn resolve-modifiers [g]
  (reduce (fn [g [node-id _]]
            (let [in-edge (get-in-edge g node-id)
                  child (some #(when (= :child (:role (attrs g %))) (graph/dest %)) (graph/out-edges g node-id))
                  modifier (some #(when (= :modifier (:role (attrs g %))) (graph/dest %)) (graph/out-edges g node-id))]
              (-> g
                  (resolve-modifier modifier)
                  (graph/remove-nodes node-id)
                  (graph/add-edges [^:edge (graph/src in-edge) child (attrs g in-edge)]))))
          g
          (find-nodes g {:type :modifier})))
