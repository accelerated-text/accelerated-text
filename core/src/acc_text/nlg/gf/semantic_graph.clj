(ns acc-text.nlg.gf.semantic-graph
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [clojure.tools.logging :as log]))

(defn drop-non-semantic-parts [semantic-graph-instance]
  (-> semantic-graph-instance
      (update ::sg/concepts #(remove (fn [{type ::sg/type}] (contains? #{:segment :document-plan} type)) %))
      (update ::sg/relations #(remove (fn [{role ::sg/role}] (contains? #{:segment :instance} role)) %))))

(defn concepts-with-type [{concepts ::sg/concepts} concept-type]
  (filter (fn [{type ::sg/type}] (= concept-type type)) concepts))

(defn relations-with-role [{relations ::sg/relations} relation-role]
  (filter (fn [{role ::sg/role}] (= role relation-role)) relations))

(defn root-relation [{relations ::sg/relations} concept-table]
  (let [{to ::sg/to} (first (filter (fn [{role ::sg/role}] (= :segment role)) relations))
        root-concept-rel (->> relations
                              (filter (fn [{from ::sg/from}] (= to from)))
                              ;;FIXME. For now I assume that only one AMR will be present in Segment
                              (first))]
    (get concept-table (::sg/to root-concept-rel))))

(defn start-category-graph
  "Get the sub-graph immediately bellow starting (the one under Segment) category"
  [semantic-graph concept-table]
  (let [{start-id ::sg/id :as start-cat} (root-relation semantic-graph concept-table)]
    #::sg{:concepts  [start-cat]
          :relations (filter (fn [{from ::sg/from}] (= from start-id)) (::sg/relations semantic-graph))}))

(defn concepts->concept-map
  "Take semantic graph and produce a map of concept id to a concept item.
  Useful when later we do analysis based on relations where only concept ID is present"
  [{concepts ::sg/concepts :as item}]
  (reduce (fn [m {id ::sg/id :as concept}]
            (assoc m id concept))
          {}
          concepts))

(defn relations-with-concepts
  "Take graph relation triplet and instead of just ID embed the full concept map.
  Going from [from-id to-id] to [from-concept-map to-concept-map]"
  [semantic-graph concept-table edge-role]
  (for [{from ::sg/from to ::sg/to} (relations-with-role semantic-graph edge-role)]
    [(get concept-table from) (get concept-table to)]))
