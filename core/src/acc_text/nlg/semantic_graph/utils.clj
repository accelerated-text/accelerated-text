(ns acc-text.nlg.semantic-graph.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.set :as set]
            [ubergraph.core :as uber])
  (:import (java.util UUID)))

(defn find-descendant-ids [{relations ::sg/relations} ids]
  (let [relation-map (group-by :from relations)]
    (loop [ids ids descendant-ids #{}]
      (if-not (seq ids)
        descendant-ids
        (let [child-ids (->> ids (mapcat relation-map) (map :to) (into #{}))]
          (recur child-ids (set/union descendant-ids child-ids)))))))

(defn get-child-with-relation [{::sg/keys [concepts relations]} {id :id} role]
  (let [concept-map (zipmap (map :id concepts) concepts)
        relation-map (group-by :from relations)]
    (->> (get relation-map id)
         (some #(when (= role (:role %)) %))
         (:to)
         (get concept-map))))

(defn get-children [{::sg/keys [concepts relations]} {id :id}]
  (let [concept-map (zipmap (map :id concepts) concepts)
        relation-map (group-by :from relations)]
    (map #(get concept-map (:to %)) (get relation-map id))))

(defn get-concepts-with-type [{concepts ::sg/concepts} type]
  (filter #(= type (:type %)) concepts))

(defn prune-branches [semantic-graph ids]
  (let [ids-incl-descendants (set/union (set ids) (find-descendant-ids semantic-graph ids))]
    (-> semantic-graph
        (update ::sg/concepts (fn [concepts]
                                (remove #(contains? ids-incl-descendants (:id %)) concepts)))
        (update ::sg/relations (fn [relations]
                                 (remove #(contains? ids-incl-descendants (:to %)) relations))))))

(defn prune-concepts-by-type [semantic-graph type]
  (->> (get-concepts-with-type semantic-graph type)
       (map :id)
       (into #{})
       (prune-branches semantic-graph)))

(defn prune-nil-relations [semantic-graph]
  (update semantic-graph ::sg/relations (fn [relations]
                                          (remove (fn [{:keys [from to]}]
                                                    (or (nil? from) (nil? to)))
                                                  relations))))

(defn prune-unrelated-branches [{::sg/keys [concepts relations] :as semantic-graph}]
  (prune-branches semantic-graph (set/difference (into #{} (map :id (rest concepts)))
                                                 (into #{} (map :to relations)))))

(defn semantic-graph->ubergraph [{::sg/keys [concepts relations]}]
  (let [id->uuid (zipmap (map :id concepts) (repeatedly #(UUID/randomUUID)))]
    (apply uber/digraph (concat
                          (map (fn [{:keys [id] :as concept}]
                                 [^:node (id->uuid id) (dissoc concept :id)])
                               concepts)
                          (map (fn [{:keys [from to] :as relation}]
                                 [^:edge (id->uuid from) (id->uuid to) (dissoc relation :from :to)])
                               relations)))))
