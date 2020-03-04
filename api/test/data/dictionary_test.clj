(ns data.dictionary-test
  (:require [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dictionary]))

(def test-dictionary-items #{#:acc-text.nlg.dictionary.item{:id       "place_Eng"
                                                            :key      "place_1_N"
                                                            :sense    "1"
                                                            :category "N"
                                                            :language "Eng"
                                                            :forms    ["place" "places"]}
                             #:acc-text.nlg.dictionary.item{:id       "place_Ger"
                                                            :key      "place_1_N"
                                                            :sense    "1"
                                                            :category "N"
                                                            :language "Ger"
                                                            :forms    ["platz" "plätze"]}})

(defn prepare-environment [f]
  (doseq [item test-dictionary-items]
    (dictionary/write-item item))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration list-dictionary-items
  (is (= test-dictionary-items (into #{} (dictionary/list-items)))))

(deftest ^:integration search-dictionary-items
  (is (= #{} (into #{} (dictionary/scan #{} #{}))))
  (is (= #{} (into #{} (dictionary/scan #{} #{}))))
  (is (= #{#:acc-text.nlg.dictionary.item{:id       "place_Eng"
                                          :key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Eng"
                                          :forms    ["place" "places"]}
           #:acc-text.nlg.dictionary.item{:id       "place_Ger"
                                          :key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Ger"
                                          :forms    ["platz" "plätze"]}}
         (into #{} (dictionary/scan #{"place_1_N"} #{"Eng" "Ger"}))))
  (is (= #{#:acc-text.nlg.dictionary.item{:id       "place_Eng"
                                          :key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Eng"
                                          :forms    ["place" "places"]}}
         (into #{} (dictionary/scan #{"place_1_N"} #{"Eng"}))))
  (is (= #{#:acc-text.nlg.dictionary.item{:id       "place_Ger"
                                          :key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Ger"
                                          :forms    ["platz" "plätze"]}}
         (into #{} (dictionary/scan #{"place_1_N"} #{"Ger"})))))
