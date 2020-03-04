(ns api.nlg.context
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.dictionary.item :as dictionary-item]
            [data.entities.amr :as amr]
            [data.entities.rgl :as rgl]
            [data.entities.dictionary :as dict-entity]))

(defn get-values [semantic-graph type]
  (->> (get semantic-graph ::sg/concepts)
       (filter #(= type (:type %)))
       (map :value)
       (set)))

(defn build-dictionary-context [semantic-graph languages]
  (->> languages
       (dict-entity/scan (get-values semantic-graph :dictionary-item))
       (group-by ::dictionary-item/language)))

(defn build-amr-context [semantic-graph]
  (reduce (fn [m amr-id]
            (let [{sg :semantic-graph :as amr} (or (amr/get-amr amr-id) (rgl/get-rgl amr-id))]
              (cond-> m
                      (some? amr) (-> (assoc amr-id amr)
                                      (cond-> (some? sg) (merge (build-amr-context sg)))))))
          {}
          (get-values semantic-graph :amr)))

(defn build-context
  ([semantic-graph]
   (build-context semantic-graph {:languages [(dict-entity/default-language)] :data {}}))
  ([semantic-graph {:keys [languages data]}]
   {:amr        (build-amr-context semantic-graph)
    :data       data
    :dictionary (build-dictionary-context semantic-graph languages)}))
