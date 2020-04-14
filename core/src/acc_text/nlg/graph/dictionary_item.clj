(ns acc-text.nlg.graph.dictionary-item
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.graph.utils :refer [find-nodes]]
            [loom.graph :as graph]))

(defn get-dictionary-item [dictionary language key]
  (if (contains? dictionary key)
    (get dictionary key)
    (throw (Exception. (format "Missing dictionary item for `%s` language: `%s`" language key)))))

(defn resolve-dictionary-items [g {dictionary :dictionary {lang "*Language"} :constants}]
  (reduce (fn [g [node-id {key :label}]]
            (if-let [{::dictionary-item/keys [category language forms attributes]} (get-dictionary-item dictionary lang key)]
              (update-in g [:attrs node-id] #(merge % {:category   category
                                                       :language   language
                                                       :forms      forms
                                                       :attributes (or attributes {})}))
              (graph/remove-nodes g node-id)))
          g
          (find-nodes g {:type :dictionary-item})))
