(ns nlg.api.util-test
  (:require [clojure.test :refer :all]
            [nlg.api.utils :refer :all]))

(deftest test-read-stub-csv
  (let [data (read-stub-csv)
        first-row (first data)]
    (is (not (empty? data)))
    (is (= "premium lacing" (first-row :Lacing)))))
