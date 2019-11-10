(ns api.nlg.semantic-graph
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [api.nlg.dictionary :as dictionary-api]
            [api.utils :as utils]
            [clojure.string :as str]
            [data.entities.amr :as amr]))

(defn get-dictionary-items [semantic-graph]
  (->> (get semantic-graph ::sg/concepts)
       (filter (fn [{type ::sg/type}]
                 (= type :dictionary-item)))
       (map ::sg/value)
       (set)))

(defn build-dictionary-for-profile [semantic-graph reader-profile]
  (reduce (fn [m value]
            (assoc m value (dictionary-api/search (str/lower-case value) reader-profile)))
          {}
          (get-dictionary-items semantic-graph)))

(defn build-dictionary [semantic-graph reader-profiles]
  (reduce (fn [m reader-profile]
            (assoc m reader-profile (build-dictionary-for-profile semantic-graph reader-profile)))
          {}
          reader-profiles))

(defmulti add-context (fn [concept _] (get concept ::sg/type)))

(defmethod add-context :default [concept _] concept)

(defmethod add-context :dictionary-item [{value ::sg/value :as concept} {dictionary ::sg/dictionary reader-profile ::sg/reader-profile}]
  (-> concept
      (assoc ::sg/members (get dictionary value))
      (assoc-in [::sg/attributes ::sg/reader-profile] reader-profile)))

(defmethod add-context :amr [{value ::sg/value :as concept} _]
  (assoc-in concept [::sg/attributes ::sg/syntax] (->> value (amr/get-verbclass) (:frames) (map :syntax))))

(defn ->instance-id [document-plan-id reader-profile]
  (keyword (str/join "-" (remove nil? [document-plan-id (when (some? reader-profile) (name reader-profile))]))))

(defn build-instances
  ([semantic-graph]
   (build-instances semantic-graph (utils/gen-uuid)))
  ([semantic-graph document-plan-id]
   (build-instances semantic-graph document-plan-id [:default]))
  ([semantic-graph document-plan-id reader-profiles]
   (let [dictionary (build-dictionary semantic-graph reader-profiles)]
     (for [reader-profile reader-profiles]
       (let [context #::sg{:document-plan-id document-plan-id
                           :reader-profile   reader-profile
                           :dictionary       (get dictionary reader-profile)}]
         #::sg{:id      (->instance-id document-plan-id reader-profile)
               :context context
               :graph   (update semantic-graph ::sg/concepts (partial map (fn [concept]
                                                                            (add-context concept context))))})))))
