(ns graphql.dictionary-test
  (:require [clojure.test :refer :all]
            [graphql.core :as graph]
            [jsonista.core :as json]))

(defn normalize-resp [resp]
  (-> resp (json/write-value-as-string) (json/read-value)))

(deftest ^:integration list-dictionary
  (let [result (normalize-resp (graph/nlg {:query "{dictionary{items{name} totalCount}}"}))
        expected {"data" {"dictionary" {"totalCount" 3 "items" [{"name" "provides"} {"name" "see"} {"name" "redesigned"}]}}}]
    (is (= expected result))))
