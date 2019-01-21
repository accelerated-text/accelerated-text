(ns lt.tokenmill.nlg.generator.planner-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.planner :refer :all]))

(deftest test-compile-single-node-plan
  (testing "Create a single subject plan"
    (let [document-plan {:type "Document-plan"
                         :statements [{:type "Segment"
                                       :textType "description"
                                       :children [{:type "Product"
                                                   :name {:type "Cell"
                                                          :name "product-name"}}]}]}
          compiled (parse-document-plan document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
            data {"product-name" "TestSubj"}
            expected {:subj "TestSubj" :verb nil :objs [] :adverb nil}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result))))))
  (testing "Create subject with two features"
    (let [document-plan {:type "Document-plan"
                         :statements [{:type "Segment"
                                       :textType "description"
                                       :children [{:type "Product"
                                                   :name {:type "Cell"
                                                          :name "product-name"}
                                                   :children [{:type "Relationship"
                                                               :relationshipType "provides"
                                                               :children [{:type "Cell"
                                                                           :name "main-feature"}
                                                                          {:type "Cell"
                                                                           :name "secondary-feature"}]}]}]}]}
          compiled (parse-document-plan document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
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
    (let [document-plan {:type "Document-plan"
                         :statements [{:type "Segment"
                                       :textType "description"
                                       :children [{:type "Product"
                                                   :name {:type "Cell"
                                                          :name "product-name"}
                                                   :children [{:type "Relationship"
                                                               :relationshipType "provides"
                                                               :children [{:type "Cell"
                                                                           :name "main-feature"}
                                                                          {:type "Cell"
                                                                           :name "secondary-feature"}
                                                                          {:type "Cell"
                                                                           :name "other-feature"}]}
                                                              {:type "Rhetorical"
                                                               :rstType "elaboration"
                                                               :children [{:type "Cell"
                                                                           :name "style"}]
                                                               }]}
                                                  {:type "Product-Component"
                                                   :name {:type "Cell"
                                                          :name "lacing"}
                                                   :children [{:type "Relationship"
                                                               :relationshipType "consequence"
                                                               :children [{:type "Quote"
                                                                           :text "a snug fit for everyday wear"}]}]}]}]}

          data {"product-name" "Nike Air"
                "main-feature" "comfort"
                "secondary-feature" "support"
                "other-feature" "style"
                "lacing" "premium lacing"
                "style" "with sleek update on a classic design"}
          result (render-dp document-plan data)
          expected-any ["Nike Air provides comfort, support and style with sleek update on a classic design. Premium lacing results in a snug fit for everyday wear."
                        "Nike Air provides comfort, support and style with sleek update on a classic design. Premium lacing results in a situation where laces never gets into a knot."]]
      (println "Result: " result)
      (is (some #(= % result) expected-any)))))


;; (deftest compile-advanced-case
;;   (testing "Testing some different sentence structure"
;;     (let [document-plan {:items [{:type "Product"
;;                                   :name {:attribute "product-name"}
;;                                   :purposes [{:relationship "provide"
;;                                               :value {:type "Attribute"
;;                                                       :attribute "main-feature"
;;                                                       :modifier {:synset-id :good-shoe}
;;                                                       :selector {:min 2 :max 4}
;;                                                       :format {:type "Currency" :value "EUR"}
;;                                                       :elaborate {:type :quote
;;                                                                   :quote "lasts as long as you can run"}}}]}
;;                                  {:type "Component"
;;                                   :name {:attribute "cushioning-name"}
;;                                   :purposes [{:relationship "is"
;;                                               :value {:type "All"
;;                                                       :attributes [{:attribute "quote1"}
;;                                                                    {:attribute "quote2"}]}}]}]}
;;           data {"product-name" "Nike Odyssey React Men's Running Shoe"
;;                 "main-feature" "comfort"
;;                 "cushioning-name" "Nike React foam cushioning"
;;                 "quote1" "responsive yet lightweight"
;;                 "quote2" "durable yet soft"}
;;           result (render-dp document-plan data)
;;           expected "The Nike Odyssey React Men's Running Shoe provides crazy comfort that lasts as long as you can run. Its Nike React foam cushioning is responsive yet lightweight, durable yet soft."]
;;       (println "Result: " result)))
;;   (testing "Interesting case"
;;     (let [document-plan {:items [{:type "Product"
;;                                   :quote "Inspired by the look of the late '90s, specifically the shoes featured in the Nike Alpha Project"
;;                                   :name {:attribute "product-name"}
;;                                   :purposes [{:relationship :provide
;;                                               :value {:type :all
;;                                                       :attributes [{:attribute "main-feature"}
;;                                                                    {:attribute "secondary-feature"}]}
;;                                               }]
;;                                   }]}
;;           data {"product-name" "Nike Zoom 2K"
;;                 "main-feature" "heritage style"
;;                 "secondary-feature" "modern comfort of Zoom Air cushioning"}
;;           ;; result (render-dp document-plan data)
;;           expected "Inspired by the look of the late '90s, specifically the shoes featured in the Nike Alpha Project, the Nike Zoom 2K combines heritage style and the modern comfort of Zoom Air cushioning"])))
