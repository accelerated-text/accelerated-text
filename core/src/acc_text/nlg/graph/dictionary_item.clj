(ns acc-text.nlg.graph.dictionary-item
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.graph.utils :refer [find-nodes]]))

(defn get-dictionary-item [dictionary language key category]
  (if (contains? dictionary [key category])
    (get dictionary [key category])
    (throw (Exception. (format "Missing dictionary item for `%s` language category `%s`: `%s`"
                               language category key)))))

(defn add-dictionary-item [g node-id dictionary-item]
  (let [{::dictionary-item/keys [category language forms attributes]} dictionary-item]
    (update-in g [:attrs node-id] #(merge % {:type       :dictionary-item
                                             :category   category
                                             :language   language
                                             :forms      forms
                                             :attributes (or attributes {})}))))

(defn resolve-dictionary-items [g {dictionary :dictionary {lang "*Language"} :constants}]
  (reduce (fn [g [node-id {key :label category :category}]]
            (add-dictionary-item g node-id
                                 (get-dictionary-item dictionary lang key category)))
          g
          (find-nodes g {:type :dictionary-item})))
