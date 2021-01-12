(ns data.test-utils
    (:require
              [clojure.test :refer [deftest is use-fixtures]]
              [data.utils :as utils]))

(deftest test-jaccard-distance
  (is (= 2/3 (utils/jaccard-distance
              {:primary 1 :secondary 3}
              {:primary 2 :secondary 3}))))