(ns acc-text.nlg.graph.dictionary-item
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.graph.utils :refer [find-nodes]]))

(defn get-dictionary-item [dictionary language key]
  (if (contains? dictionary key)
    (get dictionary key)
    (throw (Exception. (format "Missing dictionary item for `%s` language: `%s`" language key)))))

(defn add-dictionary-item [g node-id dictionary-item]
  (let [{::dictionary-item/keys [category language forms attributes]} dictionary-item]
    (update-in g [:attrs node-id] #(merge % {:type       :dictionary-item
                                             :category   category
                                             :language   language
                                             :forms      forms
                                             :attributes (or attributes {})}))))

(defn resolve-dictionary-items [g {dictionary :dictionary {lang "*Language"} :constants}]
  (reduce (fn [g [node-id {key :label}]]
            (->> key
                 (get-dictionary-item dictionary lang)
                 (add-dictionary-item g node-id)))
          g
          (find-nodes g {:type :dictionary-item})))
