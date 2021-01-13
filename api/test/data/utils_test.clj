(ns data.utils-test
    (:require [clojure.test :refer [deftest is use-fixtures]]
              [data.utils :as utils]))

(deftest test-jaccard-distance
  (is (= 2/3 (utils/jaccard-distance
              {:primary 1 :secondary 3}
              {:primary 2 :secondary 3}))))

(deftest test-selected-rows
  (let [rows          [["test1" "test2"] ["test2" "test2"] ["test3" "test3"] ["test3" "test4"] ["test3" "test3"]]
        m             (utils/distance-matrix rows)
        selected-rows (utils/select-rows m rows 2)]
    (is (= [["test1" "test2"] ["test3" "test3"]] selected-rows))))

(deftest test-sample
  (is (= [0 20 40 60 80] (utils/sample (range 100) 5))))