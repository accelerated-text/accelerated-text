(ns acc-text.nlg.graph.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [loom.alg :as alg]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [ubergraph.core :as uber]
            [clojure.set :as set]))

(defn id-seq []
  (map #(keyword (format "%02d" %)) (rest (range))))

(defn add-edges [g edges]
  (apply graph/add-edges g edges))

(defn remove-nodes [g nodes]
  (apply graph/remove-nodes g nodes))

(defn remove-edges [g edges]
  (apply graph/remove-edges g edges))

(defn find-nodes [g query]
  (->> (graph/nodes g)
       (map (partial uber/node-with-attrs g))
       (filter (fn [[_ node]] (= query (select-keys node (keys query)))))))

(defn find-edges [g query]
  (->> (graph/edges g)
       (map (partial uber/edge-with-attrs g))
       (filter (fn [[_ _ edge]] (= query (select-keys edge (keys query)))))))

(defn get-successors [g node-id]
  (->> node-id
       (graph/out-edges g)
       (sort-by #(:index (attrs g %)))
       (map graph/dest)))

(defn get-predecessors [g node-id]
  (->> node-id
       (graph/in-edges g)
       (sort-by #(:index (attrs g %)))
       (map graph/src)))

(defn get-in-edge [graph node-id]
  (first (graph/in-edges graph node-id)))

(defn find-root-id [g]
  (ffirst (find-nodes g {:type :document-plan})))

(defn prune-graph [g]
  (apply graph/remove-nodes g
         (set/difference
           (into #{} (graph/nodes g))
           (into #{} (alg/bf-traverse g (find-root-id g))))))

(defn add-concept-position [g]
  (reduce (fn [g [node-id position]]
            (update-in g [:attrs node-id] #(assoc % :position position)))
          g
          (zipmap (alg/pre-traverse g (find-root-id g)) (rest (range)))))

(defn ubergraph->semantic-graph [g & {:keys [keep-ids?]}]
  (let [{:keys [nodes directed-edges]} (uber/ubergraph->edn g)
        uuid->id (if (true? keep-ids?) identity (zipmap (alg/pre-traverse g (find-root-id g)) (id-seq)))]
    #::sg{:relations (->> directed-edges
                          (map (fn [[from to relation]]
                                 (merge {:from (uuid->id from) :to (uuid->id to)} relation)))
                          (sort-by :from))
          :concepts  (->> nodes
                          (map (fn [[id concept]]
                                 (merge {:id (uuid->id id)} concept)))
                          (sort-by :id))}))

(defn save-graph [graph filename]
  (uber/viz-graph graph {:auto-label true :save {:format :png :filename filename}}))
