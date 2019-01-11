(ns lt.tokenmill.nlg.generator.planner-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.planner :refer :all]))

(deftest test-compile-single-node-plan
  (testing "Create a single subject plan"
    (let [document-plan {:items [{:type "Product"}]}
          compiled (compile-dp document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [concrete-plan (first compiled)
            data {:name "TestSubj"}
            expected {:subj "TestSubj"}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result)))))))
