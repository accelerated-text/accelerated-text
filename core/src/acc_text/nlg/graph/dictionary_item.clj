(ns acc-text.nlg.graph.dictionary-item
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [acc-text.nlg.graph.utils :refer [find-nodes]]))

(defn get-dictionary-item [dictionary language key category]
  (if (contains? dictionary [key category])
    (get dictionary [key category])
    (throw (Exception. (format "Missing dictionary item for `%s` language category `%s`: `%s`"
                               language category key)))))

(defn add-dictionary-item [g node-id dictionary-item]
  (let [{::dict-item/keys [category language forms attributes]} dictionary-item]
    (update-in g [:attrs node-id] #(merge % {:type       :dictionary-item
                                             :category   category
                                             :language   language
                                             :forms      (map ::dict-item-form/value forms)
                                             :attributes (or attributes {})}))))

(defn resolve-dictionary-items [g {dictionary :dictionary {lang "*Language"} :constants}]
  (reduce (fn [g [node-id {key :label name :name category :category}]]
            (add-dictionary-item g node-id
                                 (get-dictionary-item dictionary lang (or key name) category)))
          g
          (find-nodes g {:type :dictionary-item})))
