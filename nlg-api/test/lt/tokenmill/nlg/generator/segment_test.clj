(ns lt.tokenmill.nlg.generator.segment-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.segment :refer :all]
            [lt.tokenmill.nlg.generator.templates :refer :all]))

(deftest test-dummy-generator
  (testing "Generate 'Nike Air Max 95 Premium' example"
    (let [product-name "Nike Air Max 95 Premium"
          rel "provide"
          adverb "exceptional"
          att1 "support"
          att2 "comfort"
          expected "The Nike Air Max 95 Premium provides exceptional support and comfort."]
      (is (= expected (product-1 product-name rel adverb [att1 att2])))))
  (testing "Call correct template with given parameters"
    (let [data {:product-name "Nike Air Max 95 Premium"
                :relation "provide"
                :adverb "exceptional"
                :features ["support" "comfort"]}
          expected "The Nike Air Max 95 Premium provides exceptional support and comfort."]
      (is (= expected (generate-text data))))))

