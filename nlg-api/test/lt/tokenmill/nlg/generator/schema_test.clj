(ns lt.tokenmill.nlg.generator.schema-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.schemas :refer :all]))

(defn lazy-contains? [col key]
  (some #{key} col))

(deftest test-selecting-correct-templates
  (testing "Should select `product-template-1`"
    (let [data {:product-name "Nike Air"
                :relation "give"
                :adverb "superb"
                :features ["comfort" "style"]}
          selected-templates (doall (matching-templates data))]
      (is (not (empty? selected-templates)))
      (is (lazy-contains? (map #(% :schema) selected-templates) product-schema-1)))))
