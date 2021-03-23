(ns acc-text.nlg.graph.amr
  (:require [acc-text.nlg.gf.operations :as ops]
            [acc-text.nlg.graph.utils :refer [find-root-id find-nodes add-edges remove-nodes]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [loom.graph :as graph]
            [ubergraph.core :as uber]))

(defn get-out-edge-map [g node]
  (group-by (fn [{edge-id :id}]
              (let [{:keys [name category]} (get-in g [:attrs edge-id])]
                (or name category)))
            (graph/out-edges g node)))

(defn get-amrs-with-args [g]
  (map (fn [[node attrs]]
         [node attrs (get-out-edge-map g node)])
       (find-nodes g {:type :amr})))

(defn remove-redundant-edges [g context amrs-with-args]
  (->> amrs-with-args
       (filter (fn [[_ {amr-name :name} _]] (contains? (:amr context) amr-name)))
       (mapcat (fn [[_ _ edges]] (apply concat (vals edges))))
       (graph/remove-edges* g)))

(defn substitute-operations [g amrs-with-args]
  (->> amrs-with-args
       (filter (fn [[_ {amr-name :name} _]] (contains? ops/operation-map amr-name)))
       (reduce (fn [g [node {amr-name :name} _]]
                 (update-in g [:attrs node] (fn [_]
                                              (select-keys (get ops/operation-map amr-name)
                                                           [:type :name :category :module]))))
               g)))

(defn build-amr [[node {amr-name :name category :category} amr-args] context]
  (let [amr (semantic-graph->ubergraph (get-in context [:amr amr-name]))
        amr-root (find-root-id amr)
        references (filter (fn [[_ {reference-name :name}]]
                             (contains? amr-args reference-name))
                           (find-nodes amr {:type :reference}))]
    (-> amr
        (substitute-operations (get-amrs-with-args amr))
        (assoc-in [:attrs amr-root :type] :amr-plan)
        (add-edges (cons
                     [^:edge node amr-root {:role :pointer :category category}]
                     (for [[reference-id {reference-name :name}] references
                           {id :id src :src} (graph/in-edges amr reference-id)
                           {dest :dest} (get amr-args reference-name)]
                       [^:edge src dest (get-in amr [:attrs id])])))
        (remove-nodes (map first references)))))

(defn build-operation-graph [g context]
  (let [amrs-with-args (get-amrs-with-args g)]
    (cons
      (-> g
          (substitute-operations amrs-with-args)
          (remove-redundant-edges context amrs-with-args))
      (->> amrs-with-args
           (filter (fn [[_ {amr-name :name} _]] (contains? (:amr context) amr-name)))
           (map #(build-amr % context))))))

(defn attach-amrs [g context]
  (let [amrs-with-args (get-amrs-with-args g)]
    (apply
      uber/multidigraph
      (-> g
          (substitute-operations amrs-with-args)
          (remove-redundant-edges context amrs-with-args))
      (->> amrs-with-args
           (filter (fn [[_ {amr-name :name} _]] (contains? (:amr context) amr-name)))
           (pmap (comp #(build-operation-graph % context) #(build-amr % context)))
           (apply concat)))))
