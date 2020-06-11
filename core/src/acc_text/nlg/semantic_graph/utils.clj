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

(defn find-root [{::sg/keys [concepts]}]
  (some #(when (= :document-plan (:type %)) %) concepts))

(defn get-children [{::sg/keys [concepts relations]} {id :id}]
  (let [concept-map (zipmap (map :id concepts) concepts)]
    (map (comp concept-map :to) (sort-by :index (filter #(= id (:from %)) relations)))))

(defn get-concepts-with-type [{concepts ::sg/concepts} type]
  (filter #(= type (:type %)) concepts))

(defn get-dictionary-keys [semantic-graph]
  (->> (get-concepts-with-type semantic-graph :dictionary-item)
       (map :label)
       (into #{})))

(defn get-amr-ids [semantic-graph]
  (->> (get-concepts-with-type semantic-graph :amr)
       (map :name)
       (into #{})))

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

(defn find-terminal-concepts
  ([semantic-graph]
   (let [{::sg/keys [concepts relations]} semantic-graph
         concept-map (zipmap (map :id concepts) concepts)]
     (map concept-map (set/difference (set (map :id concepts)) (set (map :from relations))))))
  ([semantic-graph type]
   (filter #(= type (:type %)) (find-terminal-concepts semantic-graph))))

(defn find-roles [{::sg/keys [relations] :as semantic-graph}]
  (let [to-relation-map (group-by :to relations)]
    (map (fn [{:keys [id name category]}]
           (let [category (or
                            category
                            (some (fn [{cat :category}]
                                    (when (some? cat) cat))
                                  (get to-relation-map id)))]
             (cond-> {:id   id
                      :name name}
                     (some? category) (assoc :category category))))
         (find-terminal-concepts semantic-graph :reference))))

(defn merge-semantic-graphs [& graphs]
  (-> (first graphs)
      (assoc ::sg/concepts (sort-by :id (mapcat ::sg/concepts graphs)))
      (assoc ::sg/relations (sort-by #(vector (:from %) (:to %)) (mapcat ::sg/relations graphs)))))

(defn add-category [{concepts ::sg/concepts :as semantic-graph}]
  (let [category (some (fn [{:keys [type category]}]
                         (when (= :document-plan type) category))
                       concepts)]
    (cond-> semantic-graph
            (some? category) (assoc ::sg/category category))))

(defn remove-nil-categories [semantic-graph]
  (-> semantic-graph
      (update ::sg/concepts #(map (fn [{cat :category :as concept}]
                                    (cond-> concept
                                            (nil? cat) (dissoc :category)))
                                  %))
      (update ::sg/relations #(map (fn [{cat :category :as relation}]
                                     (cond-> relation
                                             (nil? cat) (dissoc :category)))
                                   %))))

(defn semantic-graph->ubergraph [{::sg/keys [concepts relations]} & {:keys [keep-ids?]}]
  (let [id->uuid (zipmap (map :id concepts) (if-not (true? keep-ids?) (repeatedly #(UUID/randomUUID)) (map :id concepts)))]
    (apply uber/multidigraph (concat
                               (map (fn [{:keys [id] :as concept}]
                                      [^:node (id->uuid id) (dissoc concept :id)])
                                    concepts)
                               (map (fn [{:keys [from to] :as relation}]
                                      [^:edge (id->uuid from) (id->uuid to) (dissoc relation :from :to)])
                                    relations)))))

(defn vizgraph [semantic-graph]
  (-> semantic-graph
      (semantic-graph->ubergraph :keep-ids? true)
      (uber/viz-graph {:auto-label true})))
