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
            expected {:subj "TestSubj" :verb nil :objs [] :adverb nil}]
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
                      :verb "provide"
                      :adverb nil}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result))))))
  (testing "Generate a simple text"
    (let [document-plan {:items [{:type "Product"
                                  :name {:attribute "product-name"}
                                  :purposes [{:relationship "provide"
                                              :value {:type "All"
                                                      :attributes [{:attribute "main-feature"}
                                                                   {:attribute "secondary-feature"}]}}]
                                  :elaborate {:type "Attribute"
                                              :attribute "style"}}
                                 {:type "Component"
                                  :name {:attribute "lacing"}
                                  :purposes [{:relationship "result in"
                                              :value {:type "Any-of"
                                                      :quotes [{:quote "a snug fit for everyday wear"}
                                                               {:quote "a situation where laces never gets into a knot"}]}}]}]}
          data {"product-name" "Nike Air"
                "main-feature" "comfort"
                "secondary-feature" "support"
                "lacing" "premium lacing"
                "style" "with sleek update on a classic design"}
          result (render-dp document-plan data)
          expected-any ["Nike Air provides comfort and support with sleek update on a classic design. Premium lacing results in a snug fit for everyday wear."
                        "Nike Air provides comfort and support with sleek update on a classic design. Premium lacing results in a situation where laces never gets into a knot."]]
      (println "Result: " result)
      (is (some #(= % result) expected-any)))))
