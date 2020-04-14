(ns acc-text.nlg.graph.data
  (:require [acc-text.nlg.graph.utils :refer [find-nodes]]
            [acc-text.nlg.graph.dictionary-item :refer [get-dictionary-item add-dictionary-item]]))

(defn get-data [data key]
  (if (contains? data key)
    (get data key)
    (throw (Exception. (format "Missing value for data cell: `%s`" key)))))

(defn resolve-data [g {data :data dictionary :dictionary {lang "*Language"} :constants}]
  (reduce (fn [g [node-id {key :name}]]
            (let [value (get-data data key)]
              (if-not (contains? dictionary value)
                (update-in g [:attrs node-id] #(merge % {:type :quote :value value}))
                (->> value
                     (get-dictionary-item dictionary lang)
                     (add-dictionary-item g node-id)))))
          g
          (concat (find-nodes g {:type :data}))))
