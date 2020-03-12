(ns acc-text.nlg.graph.data
  (:require [acc-text.nlg.graph.utils :refer [find-nodes]]
            [loom.graph :as graph]))

(defn resolve-data [g {data :data}]
  (reduce (fn [g [node-id {name :name}]]
            (if-let [value (get data (keyword name))]
              (update-in g [:attrs node-id] #(assoc % :value value))
              (graph/remove-nodes g node-id)))
          g
          (find-nodes g {:type :data})))
