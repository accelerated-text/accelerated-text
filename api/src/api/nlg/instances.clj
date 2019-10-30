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
            (assoc m value (dictionary-api/search (str/lower-case value) reader-profile)))
          {}
          dictionary-items))

(defn build-dictionary [dictionary-items reader-profiles]
  (reduce (fn [m reader-profile]
            (assoc m reader-profile (build-dictionary-for-profile dictionary-items reader-profile)))
          {}
          reader-profiles))

(defn build-instances [document-plan reader-profiles]
  (let [dictionary-items (get-dictionary-items document-plan)
        dictionary (build-dictionary dictionary-items reader-profiles)]
    (for [reader-profile reader-profiles]
      (assoc document-plan :acctext.amr/context #:acctext.amr{:reader-profile reader-profile
                                                              :dictionary     (get dictionary reader-profile)}))))
