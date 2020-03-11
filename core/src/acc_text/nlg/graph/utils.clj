(ns acc-text.nlg.graph.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [loom.alg :as alg]
            [loom.graph :as graph]
            [ubergraph.core :as uber]))

(defn id-seq []
  (map #(keyword (format "%02d" %)) (rest (range))))

(defn add-edges [g edges]
  (apply graph/add-edges g edges))

(defn remove-nodes [g nodes]
  (apply graph/remove-nodes g nodes))

(defn find-nodes [g query]
  (->> (graph/nodes g)
       (map (partial uber/node-with-attrs g))
       (filter (fn [[_ node]] (= query (select-keys node (keys query)))))))

(defn find-edges [g query]
  (->> (graph/edges g)
       (map (partial uber/edge-with-attrs g))
       (filter (fn [[_ _ edge]] (= query (select-keys edge (keys query)))))))

(defn find-root-id [g]
  (some #(when (nil? (graph/predecessors g %)) %)
        (graph/nodes g)))

(defn ubergraph->semantic-graph [g]
  (let [{:keys [nodes directed-edges]} (uber/ubergraph->edn g)
        uuid->id (zipmap (alg/pre-traverse g (find-root-id g)) (id-seq))]
    #::sg{:relations (->> directed-edges
                          (map (fn [[from to relation]]
                                 (merge {:from (uuid->id from) :to (uuid->id to)} relation)))
                          (sort-by :from))
          :concepts  (->> nodes
                          (map (fn [[id concept]]
                                 (merge {:id (uuid->id id)} concept)))
                          (sort-by :id))}))
