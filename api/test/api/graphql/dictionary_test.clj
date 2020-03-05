(ns api.graphql.dictionary-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dict-entity]))

(defn prepare-environment [f]
  (doseq [item #{#:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                                :sense    "1"
                                                :category "N"
                                                :language "Eng"
                                                :forms    ["place" "places"]}
                 #:acc-text.nlg.dictionary.item{:key      "place_1_N"
                                                :sense    "1"
                                                :category "N"
                                                :language "Ger"
                                                :forms    ["platz" "plätze"]}}]
    (dict-entity/create-dictionary-item item))
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(deftest ^:integration query-dict-items
  (let [query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"
        {{{{items :items} :dictionary} :data errors :errors} :body}
        (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (< 0 (count items)))))
