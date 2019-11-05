(ns acc-text.nlg.gf.semantic-graph)

(defn drop-non-semantic-parts
  [{:keys [concepts relations]}]
  (assoc {}
         :concepts (remove #(get #{:segment :document-plan} (:type %)) concepts)
         :relations (remove #(get #{:segment :instance} (:role %)) relations)))

(defn concepts-with-type [{concepts :concepts} concept-type]
  (filter (fn [{:keys [type]}] (= concept-type type)) concepts))

(defn relations-with-role [{relations :relations} relation-role]
  (filter (fn [{:keys [role]}] (= role relation-role)) relations))

(defn root-relation [{:keys [relations]} concept-table]
  (let [{:keys [to]} (->> relations
                          (filter (fn [{:keys [role]}] (= :segment role)))
                          (first))
        root-concept-rel (->> relations
                              (filter (fn [{:keys [from]}] (= to from)))
                              ;;FIXME. For now I assume that only one AMR will be present in Segment
                              (first))]
    (get concept-table (:to root-concept-rel))))

(defn start-category-graph
  "Get the sub-graph immediately bellow starting (the one under Segment) category"
  [semantic-graph concept-table]
  (let [{start-id :id :as start-cat} (root-relation semantic-graph concept-table)]
    {:concepts start-cat
     :relations (filter (fn [{:keys [from]}] (= from start-id)) (:relations semantic-graph))}))

(defn concepts->id-concept
  "Take semantic graph and produce a map of concept id to a concept item.
  Useful when later we do analysis based on relations where only concept ID is present"
  [{:keys [concepts]}]
  (reduce (fn [agg c]
            (assoc agg (:id c) c))
          {} concepts))

(defn relations-with-concepts
  "Take graph relation triplet and instead of just ID embed the full concept map.
  Going from [from-id to-id] to [from-concept-map to-concept-map]"
  [semantic-graph concept-table edge-role]
  (reduce (fn [agg {:keys [from to]}]
            (conj agg [(get concept-table from) (get concept-table to)]))
          []
          (relations-with-role semantic-graph edge-role)))
