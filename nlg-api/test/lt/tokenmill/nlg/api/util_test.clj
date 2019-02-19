(ns lt.tokenmill.nlg.api.util-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.api.utils :refer :all]))


(deftest test-read-stub-csv
  (let [data (read-stub-csv)]
    (is (not (empty? data)))))
