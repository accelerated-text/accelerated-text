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

(defn attach-amr [g amr-g amr-node-id]
  (let [amr-root-id (find-root-id amr-g)
        out-edge-map (group-by (fn [{edge-id :id}]
                                 (get-in g [:attrs edge-id :name]))
                               (graph/out-edges g amr-node-id))
        reference-nodes (filter (fn [[_ {reference-name :name}]]
                                  (contains? out-edge-map reference-name))
                                (find-nodes amr-g {:type :reference}))]
    (-> (uber/build-graph amr-g g)
        (add-edges (->> reference-nodes
                        (reduce (fn [edges [reference-id {reference-name :name}]]
                                  (concat edges (for [{id :id src :src} (graph/in-edges amr-g reference-id)
                                                      {dest :dest} (get out-edge-map reference-name)]
                                                  [^:edge src dest (get-in amr-g [:attrs id])])))
                                [])
                        (concat
                          (for [{id :id src :src} (graph/in-edges g amr-node-id)
                                dest (graph/successors amr-g amr-root-id)]
                            [^:edge src dest (get-in g [:attrs id])]))))
        (remove-nodes (concat [amr-root-id amr-node-id] (map first reference-nodes))))))

(defn attach-rgl [g amr node-id]
  (update-in g [:attrs node-id] (fn [_]
                                  {:type   :operation
                                   :name   (:label amr)
                                   :module (:module amr)})))

(defn add-concept-index [g]
  (reduce (fn [g [node-id position]]
            (update-in g [:attrs node-id] #(assoc % :position position)))
          g
          (zipmap (alg/pre-traverse g (find-root-id g)) (rest (range)))))

(defn attach-amrs [g {amr-map :amr}]
  (letfn [(attach-fn [g]
            (reduce (fn [g [node-id {amr-name :name}]]
                      (let [{sg :semantic-graph frames :frames :as amr} (get amr-map amr-name)]
                        (cond-> g
                                (some? sg) (attach-amr (semantic-graph->ubergraph sg) node-id)
                                (some? frames) (attach-rgl amr node-id))))
                    g
                    (find-nodes g {:type :amr})))]
    (-> g (attach-fn) (attach-fn) (attach-fn) (add-concept-index))))

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
