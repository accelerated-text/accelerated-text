(ns api.graphql.dictionary-test
  (:require [api.test-utils :refer [q]]
            [api.db-fixtures :as fixtures]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.dictionary :as dict-entity]))

(use-fixtures :each fixtures/clean-db)

(deftest ^:integration query-dict-items
  (dict-entity/create-multilang-dict-item #:acc-text.nlg.dictionary.morphology{:key "place"
                                                                               :pos      "N"
                                                                               :language "Eng"
                                                                               :gender   :m
                                                                               :sense    :restaurant
                                                                               :inflections {:nom-sg "place"
                                                                                             :nom-pl "places"}})
  (dict-entity/create-multilang-dict-item #:acc-text.nlg.dictionary.morphology{:key "place"
                                                                               :pos      "N"
                                                                               :language "Ger"
                                                                               :gender   :m
                                                                               :sense    :restaurant
                                                                               :inflections {:nom-sg "platz"
                                                                                             :nom-pl "plätze"}})
  (let [query "{dictionary{items{name partOfSpeech phrases{id text defaultUsage readerFlagUsage{id usage flag{id name}}}}}}"
        {{{{items :items} :dictionary} :data errors :errors} :body}
        (q "/_graphql" :post {:query query})]
    (is (nil? errors))
    (is (< 0 (count items)))))
