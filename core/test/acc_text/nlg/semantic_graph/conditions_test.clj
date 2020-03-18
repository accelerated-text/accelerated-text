(ns acc-text.nlg.semantic-graph.conditions-test
  (:require [acc-text.nlg.graph.condition :as conditions]
            [clojure.test :refer [deftest testing is are]]))

(deftest arg-normalization
  (is (= [nil "" "abc" 5M 1.0M 5M 1.0M] (conditions/normalize [nil "" "abc" "5" "1.0" 5 1.0]))))

(deftest comparison
  (testing "Result is true"
    (are [operator args]
      (true? (conditions/comparison operator args))
      "=" [1 "1" "1.0"]
      "=" ["" "" " "]
      "=" ["Abc" "Abc"]
      "=" [1 "1" "1.0"]
      "!=" [1 "1" "1.1"]
      "<" [5 "6" "7"]
      "<=" [5 "5"]
      "<=" [4 "5"]
      ">" [2 "1.1" "1"]
      ">=" [2 "2"]
      ">=" [2.1 "2"]))
  (testing "Result is false"
    (are [operator args]
      (false? (conditions/comparison operator args))
      "=" [1 "1" "1.0001"]
      "=" ["" " " "."]
      "=" ["Abc" "abc"]
      "=" [1 2]
      "!=" [1 "1"]
      "<" [7 "6"]
      "<=" [5 "4.99"]
      "<=" [5.1 "5"]
      ">" [2 "1.1" "1.11"]
      ">=" ["1.99" 2]
      ">=" [1.99 "2"]))
  (testing "Edge cases"
    (is (nil? (conditions/comparison "=" [])))))

