(ns api.nlg.context
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.dictionary :as dictionary]
            [api.utils :as utils]
            [clojure.string :as str]
            [data.entities.amr :as amr]))

(defn get-values [semantic-graph type]
  (->> (get semantic-graph ::sg/concepts)
       (filter #(= (::sg/type %) type))
       (map ::sg/value)
       (set)))

(defn build-dictionary-for-profile [semantic-graph reader-profile]
  (reduce (fn [m value]
            (assoc m value (dictionary/search (str/lower-case value) reader-profile)))
          {}
          (get-values semantic-graph :dictionary-item)))

(defn build-dictionary-context [semantic-graph reader-profiles]
  (reduce (fn [m reader-profile]
            (assoc m reader-profile (build-dictionary-for-profile semantic-graph reader-profile)))
          {}
          (set reader-profiles)))

(defn build-amr-context [semantic-graph]
  (reduce (fn [m amr-id]
            (assoc m (keyword amr-id) (amr/load-single amr-id)))
          {}
          (get-values semantic-graph :amr)))

(defn build-context
  ([semantic-graph]
   (build-context semantic-graph (utils/gen-uuid)))
  ([semantic-graph document-plan-id]
   (build-context semantic-graph document-plan-id [:default]))
  ([semantic-graph document-plan-id reader-profiles]
   #::sg{:document-plan-id document-plan-id
         :reader-profiles  (set reader-profiles)
         :dictionary       (build-dictionary-context semantic-graph reader-profiles)
         :amr              (build-amr-context semantic-graph)}))
