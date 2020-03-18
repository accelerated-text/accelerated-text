(ns acc-text.nlg.graph.amr
  (:require [acc-text.nlg.graph.utils :refer [find-root-id find-nodes add-edges remove-nodes]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [loom.graph :as graph]
            [ubergraph.core :as uber]))

(defn segment->frame [g]
  (reduce (fn [g [node-id _]]
            (assoc-in g [:attrs node-id :type] :frame))
          g
          (find-nodes g {:type :segment})))

(defn attach-amr [g amr-g amr-node-id]
  (let [amr-root-id (find-root-id amr-g)
        out-edge-map (group-by (fn [{edge-id :id}]
                                 (get-in g [:attrs edge-id :name]))
                               (graph/out-edges g amr-node-id))
        reference-nodes (filter (fn [[_ {reference-name :name}]]
                                  (contains? out-edge-map reference-name))
                                (find-nodes amr-g {:type :reference}))]
    (-> (segment->frame amr-g)
        (uber/build-graph g)
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

(defn attach-amrs [g {amr-map :amr}]
  (letfn [(attach-fn [g]
            (reduce (fn [g [node-id {amr-name :name}]]
                      (let [{sg :semantic-graph frames :frames :as amr} (get amr-map amr-name)]
                        (cond
                          (some? sg) (attach-amr g (semantic-graph->ubergraph sg) node-id)
                          (some? frames) (attach-rgl g amr node-id)
                          :else (throw (Exception. (format "AMR not found in context: `%s`" amr-name))))))
                    g
                    (find-nodes g {:type :amr})))]
    (-> g (attach-fn) (attach-fn) (attach-fn))))
