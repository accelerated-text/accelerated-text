(ns lt.tokenmill.nlg.api.util-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.api.utils :refer :all]))


(deftest test-read-stub-csv
  (let [data (read-stub-csv)
        first-row (first data)]
    (is (not (empty? data)))
    (is (= "premium lacing" (first-row :Lacing)))))
