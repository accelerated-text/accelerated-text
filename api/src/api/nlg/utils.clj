(ns api.nlg.utils
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [data.entities.dictionary :as dict-entity]
            [data.entities.amr :as amr]))

(defn get-dictionary-keys [semantic-graph]
  (->> (sg-utils/get-concepts-with-type semantic-graph :dictionary-item)
       (map :label)
       (into #{})))

(defn build-dictionaries [keys languages]
  (->> languages
       (dict-entity/scan-dictionary keys)
       (group-by ::dictionary-item/language)
       (reduce-kv (fn [m k v]
                    (assoc m k (zipmap (map (fn [{::dictionary-item/keys [key category]}]
                                              [key category]) v) v)))
                  {})))

(defn fetch-amrs [semantic-graph]
  (->> (sg-utils/get-concepts-with-type semantic-graph :amr)
       (map :name)
       (into #{})
       (reduce (fn [m amr-id]
                 (let [{sg :semantic-graph :as amr} (amr/get-amr amr-id)]
                   (cond-> m
                           (some? amr) (-> (assoc amr-id amr)
                                           (cond-> (some? sg) (merge (fetch-amrs sg)))))))
               {})))
