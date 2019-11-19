(ns api.nlg.context
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.dictionary :as dictionary]
            [clojure.string :as str]
            [data.entities.amr :as amr]))

(defn get-values [semantic-graph type]
  (->> (get semantic-graph ::sg/concepts)
       (filter #(= (::sg/type %) type))
       (map ::sg/value)
       (set)))

(defn build-dictionary-context [semantic-graph reader-profile]
  (reduce (fn [m value]
            (assoc m value (dictionary/search (str/lower-case value) reader-profile)))
          {}
          (get-values semantic-graph :dictionary-item)))

(defn build-amr-context [semantic-graph]
  (reduce (fn [m amr-id]
            (assoc m (keyword amr-id) (amr/load-single amr-id)))
          {}
          (get-values semantic-graph :amr)))

(defn build-context
  ([semantic-graph]
   (build-context semantic-graph :default))
  ([semantic-graph reader-profile]
   {:reader-profile reader-profile
    :dictionary     (build-dictionary-context semantic-graph reader-profile)
    :amr            (build-amr-context semantic-graph)}))
