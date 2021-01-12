(ns data.utils-test
    (:require [clojure.test :refer [deftest is use-fixtures]]
              [data.utils :as utils]))

(deftest test-jaccard-distance
  (is (= 2/3 (utils/jaccard-distance
              {:primary 1 :secondary 3}
              {:primary 2 :secondary 3}))))

(deftest test-selected-rows
  (let [rows          [{:a 1 :b 2} {:a 2 :b 2} {:a 3 :b 3}]
        m             (utils/distance-matrix rows)
        selected-rows (utils/select-rows m rows 2)]
    (is (= [{:a 2, :b 2} {:a 3, :b 3}] selected-rows))))