(ns acc-text.nlg.graph.amr
  (:require [acc-text.nlg.gf.operations :as ops]
            [acc-text.nlg.graph.utils :refer [find-root-id find-nodes add-edges remove-nodes]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [loom.graph :as graph]
            [ubergraph.core :as uber]))

(defn attach-amr [g amr-node-id sg]
  (let [amr-g (semantic-graph->ubergraph sg)
        amr-root-id (find-root-id amr-g)
        out-edge-map (group-by (fn [{edge-id :id}]
                                 (let [{:keys [name category]} (get-in g [:attrs edge-id])]
                                   (or name category)))
                               (graph/out-edges g amr-node-id))
        reference-nodes (filter (fn [[_ {reference-name :name}]]
                                  (contains? out-edge-map reference-name))
                                (find-nodes amr-g {:type :reference}))]
    (-> (uber/build-graph g amr-g)
        (assoc-in [:attrs amr-root-id :type] :amr)
        (remove-nodes (concat [amr-node-id] (map first reference-nodes)))
        (add-edges (concat
                     (for [[reference-id {reference-name :name}] reference-nodes
                           {id :id src :src} (graph/in-edges amr-g reference-id)
                           {dest :dest} (get out-edge-map reference-name)]
                       [^:edge src dest (get-in amr-g [:attrs id])])
                     (for [{id :id src :src} (graph/in-edges g amr-node-id)]
                       [^:edge src amr-root-id (get-in g [:attrs id])]))))))

(defn attach-rgl [g node-id name]
  (update-in g [:attrs node-id] (fn [_]
                                  (select-keys (get ops/operation-map name)
                                               [:type :name :category :module]))))

(defn attach-amrs [g {amr-map :amr :as context}]
  (reduce (fn [g [node-id {amr-name :name}]]
            (let [sg (get amr-map amr-name)]
              (cond
                (nil? amr-name) g
                (contains? ops/operation-map amr-name) (attach-rgl g node-id amr-name)
                (some? sg) (-> g (attach-amr node-id sg) (attach-amrs context))
                :else (throw (Exception. (format "AMR not found in context: `%s`" amr-name))))))
          g
          (find-nodes g {:type :amr})))
