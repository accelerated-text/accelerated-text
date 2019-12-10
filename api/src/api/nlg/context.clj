(ns api.nlg.context
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.dictionary :as dictionary]
            [clojure.string :as str]
            [data.entities.amr :as amr]))

(defn get-reader-profiles [reader-model]
  (or
    (seq
      (reduce-kv (fn [acc k v]
                   (cond-> acc
                           (true? v) (conj k)))
                 []
                 reader-model))
    [:default]))

(defn get-values [semantic-graph type]
  (->> (get semantic-graph ::sg/concepts)
       (filter #(= type (:type %)))
       (map :value)
       (set)))

(defn build-dictionary-context [semantic-graph reader-profiles]
  (reduce (fn [m value]
            (assoc m value (->> reader-profiles
                                (mapcat #(dictionary/search (str/lower-case value) %))
                                (into #{})
                                (sort)
                                (vec))))
          {}
          (get-values semantic-graph :dictionary-item)))

(defn build-amr-context [semantic-graph]
  (reduce (fn [m amr-id]
            (assoc m (keyword amr-id) (amr/load-single amr-id)))
          {}
          (get-values semantic-graph :amr)))

(defn build-context
  ([semantic-graph]
   (build-context semantic-graph {:default true}))
  ([semantic-graph reader-model]
   (let [reader-profiles (get-reader-profiles reader-model)]
     {:dictionary (build-dictionary-context semantic-graph reader-profiles)
      :amr        (build-amr-context semantic-graph)})))
