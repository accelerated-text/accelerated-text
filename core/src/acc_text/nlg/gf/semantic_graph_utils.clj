(ns acc-text.nlg.gf.semantic-graph-utils
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [clojure.spec.alpha :as s]))

(defn drop-non-semantic-parts [semantic-graph-instance]
  (-> semantic-graph-instance
      (update ::sg/concepts #(remove (fn [{type ::sg/type}] (contains? #{:segment :document-plan} type)) %))
      (update ::sg/relations #(remove (fn [{role ::sg/role}] (contains? #{:segment :instance} role)) %))))

(defn concepts-with-type [{concepts ::sg/concepts} concept-type]
  (filter (fn [{type ::sg/type}] (= concept-type type)) concepts))

(defn relations-with-role [{relations ::sg/relations} relation-role]
  (filter (fn [{role ::sg/role}] (= role relation-role)) relations))

(defn concepts->concept-map
  "Take semantic graph and produce a map of concept id to a concept item.
  Useful when later we do analysis based on relations where only concept ID is present"
  [{concepts ::sg/concepts}]
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

(defn subgraph [{relations ::sg/relations concepts ::sg/concepts} {root-id ::sg/id :as root-node}]
  (let [root-node-relations (filter (fn [{from ::sg/from}] (= root-id from)) relations)
        child-ids (set (map ::sg/to root-node-relations))]
    #::sg{:relations root-node-relations
          :concepts (conj (filter (fn [{id ::sg/id}] (get child-ids id)) concepts)
                          root-node)}))

(s/fdef subgraph
  :args (s/cat :graph ::sg/graph :root-node ::sg/concept)
  :ret ::sg/graph)
