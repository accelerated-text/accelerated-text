(ns api.nlg.instances
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [api.nlg.dictionary :as dictionary-api]
            [clojure.string :as str]))

(defn get-dictionary-items [semantic-graph]
  (->> (get semantic-graph ::sg/concepts)
       (filter (fn [{type ::sg/type}]
                 (= type :dictionary-item)))
       (map ::sg/value)
       (set)))

(defn build-dictionary-for-profile [dictionary-items reader-profile]
  (reduce (fn [m value]
            (assoc m value (dictionary-api/search (str/lower-case value) reader-profile)))
          {}
          dictionary-items))

(defn build-dictionary [dictionary-items reader-profiles]
  (reduce (fn [m reader-profile]
            (assoc m reader-profile (build-dictionary-for-profile dictionary-items reader-profile)))
          {}
          reader-profiles))

(defmulti add-context (fn [concept _] (get concept ::sg/type)))

(defmethod add-context :default [concept _] concept)

(defmethod add-context :dictionary-item [{value ::sg/value :as concept} {:keys [dictionary reader-profile]}]
  (-> concept
      (assoc ::sg/members (get dictionary value))
      (assoc-in [::sg/attributes ::sg/reader-profile] reader-profile)))

(defn ->instance-id [document-plan-id {:keys [reader-profile]}]
  (str/join "-" (remove nil? [document-plan-id (when (some? reader-profile) (name reader-profile))])))

(defn build-instances [semantic-graph document-plan-id reader-profiles]
  (let [dictionary-items (get-dictionary-items semantic-graph)
        dictionary (build-dictionary dictionary-items reader-profiles)]
    (reduce (fn [m reader-profile]
              (let [context {:reader-profile reader-profile
                             :dictionary     (get dictionary reader-profile)}]
                (assoc
                  m
                  (->instance-id document-plan-id context)
                  (update semantic-graph ::sg/concepts #(map (fn [concept] (add-context concept context)) %)))))
            {}
            reader-profiles)))
