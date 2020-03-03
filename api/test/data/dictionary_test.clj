(ns data.dictionary-test
  (:require [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures testing]]
            [data.entities.dictionary :as dictionary]))

(def test-dictionary-items #{#:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                                            :sense    "1"
                                                            :category "N"
                                                            :language "Eng"
                                                            :forms    ["place" "places"]}
                             #:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                                            :sense    "1"
                                                            :category "N"
                                                            :language "Ger"
                                                            :forms    ["platz" "plätze"]}})

(defn prepare-environment [f]
  (doseq [item test-dictionary-items]
    (dictionary/create-multilang-dict-item item))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration list-dictionary-items
  (is (= test-dictionary-items (into #{} (dictionary/list-multilang-dict)))))

(deftest ^:integration search-dictionary-items
  (is (= #{} (into #{} (dictionary/search-multilang-dict #{} #{}))))
  (is (= #{} (into #{} (dictionary/search-multilang-dict #{} #{}))))
  (is (= #{#:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Eng"
                                          :forms    ["place" "places"]}
           #:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Ger"
                                          :forms    ["platz" "plätze"]}}
         (into #{} (dictionary/search-multilang-dict #{"place_1_N"} #{"Eng" "Ger"}))))
  (is (= #{#:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Eng"
                                          :forms    ["place" "places"]}}
         (into #{} (dictionary/search-multilang-dict #{"place_1_N"} #{"Eng"}))))
  (is (= #{#:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                          :sense    "1"
                                          :category "N"
                                          :language "Ger"
                                          :forms    ["platz" "plätze"]}}
         (into #{} (dictionary/search-multilang-dict #{"place_1_N"} #{"Ger"})))))
