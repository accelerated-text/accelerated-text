(ns api.nlg.context
  (:require [api.nlg.dictionary :as dictionary-api]
            [clojure.string :as str]))

(defn get-dictionary-items [document-plan]
  (->> (get document-plan :acctext.amr/concepts)
       (filter (fn [{type :acctext.amr/type}]
                 (= type :dictionary-item)))
       (map :acctext.amr/value)
       (set)))

(defn build-dictionary [document-plan reader-profile]
  (reduce (fn [m value]
            (assoc m value (dictionary-api/search (str/lower-case value) reader-profile)))
          {}
          (get-dictionary-items document-plan)))

(defn build-contexts [document-plan data reader-profile]
  (let [dictionary (build-dictionary document-plan reader-profile)]
    (for [row data]
      (assoc document-plan :acctext.amr/context #:acctext.amr{:dictionary dictionary :data row}))))
