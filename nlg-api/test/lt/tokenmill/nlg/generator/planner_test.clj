(ns lt.tokenmill.nlg.generator.planner-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.planner :refer :all]))

(deftest test-compile-single-node-plan
  (testing "Create a single subject plan"
    (let [document-plan {:items [{:type "Product" :name {:attribute "product-name"}}]}
          compiled (compile-dp document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [concrete-plan (first compiled)
            data {"product-name" "TestSubj"}
            expected {:subj "TestSubj" :verb nil :objs []}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result))))))
  (testing "Create subject with two features"
    (let [document-plan {:items [{:type "Product"
                                  :name {:attribute "product-name"}
                                  :purposes [{:relationship "provide"
                                              :value {:type "All"
                                                      :attributes [{:attribute "main-feature"}
                                                                   {:attribute "secondary-feature"}]}}
                                             ]}]}
          compiled (compile-dp document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [concrete-plan (first compiled)
            data {"product-name" "Nike Air"
                  "main-feature" "comfort"
                  "secondary-feature" "support"}
            expected {:subj "Nike Air"
                      :objs ["comfort", "support"]
                      :verb "provide"}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result))))))
  (testing "Generate a simple text from previous context"
    (let [document-plan {:items [{:type "Product"
                                  :name {:attribute "product-name"}
                                  :purposes [{:relationship "provide"
                                              :value {:type "All"
                                                      :attributes [{:attribute "main-feature"}
                                                                   {:attribute "secondary-feature"}]}}
                                             ]}]}
          data {"product-name" "Nike Air"
                "main-feature" "comfort"
                "secondary-feature" "support"}
          result (render-dp document-plan data)]
      (is (= "Nike Air provides comfort and support." result)))))
