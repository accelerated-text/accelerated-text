(ns data.row-selection-test
    (:require [clojure.test :refer [deftest is]]
              [data.entities.data-files.row-selection :as row-selection]))

(deftest test-jaccard-distance
  (is (= 2/3 (row-selection/jaccard-distance
              {:primary 1 :secondary 3}
              {:primary 2 :secondary 3}))))

(deftest test-selected-rows
  (let [rows          [["test1" "test2"] ["test2" "test2"] ["test3" "test3"] ["test3" "test4"] ["test3" "test3"]]
        m             (row-selection/distance-matrix rows)
        selected-rows (row-selection/select-rows m rows 2)]
    (is (= [["test1" "test2"] ["test3" "test3"]] selected-rows))))

(deftest test-sample
  (is (= [0 20 40 60 80] (row-selection/sample (range 100) 5))))