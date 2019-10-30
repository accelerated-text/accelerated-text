(ns api.nlg.instances
  (:require [api.nlg.dictionary :as dictionary-api]
            [clojure.string :as str]))

(defn get-dictionary-items [document-plan]
  (->> (get document-plan :acctext.amr/concepts)
       (filter (fn [{type :acctext.amr/type}]
                 (= type :dictionary-item)))
       (map :acctext.amr/value)
       (set)))

(defn build-dictionary-for-profile [dictionary-items reader-profile]
  (reduce (fn [m value]
            (assoc m value (vec (dictionary-api/search (str/lower-case value) reader-profile))))
          {}
          dictionary-items))

(defn build-dictionary [dictionary-items reader-profiles]
  (reduce (fn [m reader-profile]
            (assoc m reader-profile (build-dictionary-for-profile dictionary-items reader-profile)))
          {}
          reader-profiles))

(defmulti add-context (fn [concept _] (get concept :acctext.amr/type)))

(defmethod add-context :default [concept _] concept)

(defmethod add-context :dictionary-item [{value :acctext.amr/value :as concept} {:keys [dictionary reader-profile]}]
  (-> concept
      (assoc :acctext.amr/members (get dictionary value))
      (assoc-in [:acctext.amr/attributes :acctext.amr/reader-profile] reader-profile)))

(defn build-instances [document-plan reader-profiles]
  (let [dictionary-items (get-dictionary-items document-plan)
        dictionary (build-dictionary dictionary-items reader-profiles)]
    (for [reader-profile reader-profiles]
      (let [context {:reader-profile reader-profile
                     :dictionary     (get dictionary reader-profile)}]
        (update document-plan :acctext.amr/concepts #(mapv (fn [concept] (add-context concept context)) %))))))
