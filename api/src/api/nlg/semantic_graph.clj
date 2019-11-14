(ns api.nlg.semantic-graph
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [api.nlg.dictionary :as dictionary-api]
            [api.nlg.parser :as parser]
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
  (assoc-in concept [::sg/attributes ::sg/syntax] (->> value (amr/load-single) (:frames) (map :syntax))))

(defn ->instance-id [document-plan-id reader-profile]
  (keyword (str/join "-" (remove nil? [document-plan-id (when (some? reader-profile) (name reader-profile))]))))

(defn build-instances
  ([document-plan]
   (build-instances document-plan [:default]))
  ([document-plan reader-profiles]
   (let [semantic-graph (parser/document-plan->semantic-graph document-plan)
         dictionary (build-dictionary semantic-graph reader-profiles)
         document-plan-id (:uid document-plan)]
     (for [reader-profile reader-profiles]
       (let [context #::sg{:document-plan-id document-plan-id
                           :reader-profile   reader-profile
                           :dictionary       (get dictionary reader-profile)}]
         #::sg{:id      (->instance-id document-plan-id reader-profile)
               :context context
               :graph   (update semantic-graph ::sg/concepts (partial map #(add-context % context)))})))))
