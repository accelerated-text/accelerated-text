(ns api.nlg.context
  (:require [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [acc-text.nlg.dictionary.item :as dictionary-item]
            [data.entities.amr :as amr]
            [data.entities.rgl :as rgl]
            [data.entities.dictionary :as dict-entity]))

(defn get-dictionary-item-keys [semantic-graph]
  (->> (sg-utils/get-concepts-with-type semantic-graph :dictionary-item)
       (map :label)
       (into #{})))

(defn build-dictionary-context [keys languages]
  (->> languages
       (dict-entity/scan-dictionary keys)
       (group-by ::dictionary-item/language)
       (reduce-kv (fn [m k v]
                    (assoc m k (zipmap (map ::dictionary-item/key v) v)))
                  {})))

(defn build-amr-context [semantic-graph]
  (->> (sg-utils/get-concepts-with-type semantic-graph :amr)
       (map :name)
       (into #{})
       (reduce (fn [m amr-id]
                 (let [{sg :semantic-graph :as amr} (or (amr/get-amr amr-id) (rgl/get-rgl amr-id))]
                   (cond-> m
                           (some? amr) (-> (assoc amr-id amr)
                                           (cond-> (some? sg) (merge (build-amr-context sg)))))))
               {})))

(defn build-context
  ([semantic-graph]
   (build-context semantic-graph {:languages [(dict-entity/default-language)] :data {}}))
  ([semantic-graph {:keys [languages data]}]
   (let [amr (build-amr-context semantic-graph)]
     {:amr        amr
      :data       data
      :dictionary (build-dictionary-context (into #{} (concat
                                                        (vals data)
                                                        (get-dictionary-item-keys semantic-graph)
                                                        (mapcat #(get-dictionary-item-keys (:semantic-graph %))
                                                                (vals amr))))
                                            languages)})))
