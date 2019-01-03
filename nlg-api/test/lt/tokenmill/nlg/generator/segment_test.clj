(ns lt.tokenmill.nlg.generator.segment-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.segment :refer :all]))

(deftest test-dummy-generator
  (testing "Generate 'Nike Air Max 95 Premium' example"
    (let [product-name "Nike Air Max 95 Premium"
          rel "provides"
          att1 "support"
          att2 "comfort"
          expected "The Nike Air Max 95 Premium provides exceptional support and comfort"]
      (is (= expected (dummy-product product-name rel att1 att2))))))

