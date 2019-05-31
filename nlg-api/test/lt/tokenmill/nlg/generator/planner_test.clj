(ns lt.tokenmill.nlg.generator.planner-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.planner :refer :all]
            [lt.tokenmill.nlg.generator.parser :as parser]))

(deftest ^:integration test-compile-single-node-plan
  (testing "Create a single subject plan"
    (let [document-plan {:type "Document-plan"
                         :segments [{:type "Segment"
                                       :textType "description"
                                       :children [{:type "Product"
                                                   :name {:type "Cell"
                                                          :name "product-name"}}]}]}
          compiled (parser/parse-document-plan document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
            data {:product-name "TestSubj"}
            expected {:subj "TestSubj" :verb nil :objs [] :adverb nil}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result))))))
  (testing "Create subject with two features"
    (let [document-plan {:type "Document-plan"
                         :segments [{:type "Segment"
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
          compiled (parser/parse-document-plan document-plan)]
      (is (not (empty? compiled)))
      (is (= 1 (count compiled)))
      (let [first-segment (first compiled)
            concrete-plan (first first-segment)
            data {:product-name "Nike Air"
                  :main-feature "comfort"
                  :secondary-feature "support"}
            expected {:subj "Nike Air"
                      :objs ["comfort", "support"]
                      :verb "provide"
                      :adverb nil}]
        (println "Concrete plan: " concrete-plan)
        (let [result (build-dp-instance concrete-plan data)]
          (is (= expected result))))))
  (testing "Generate a simple text"
    (let [document-plan {:type "Document-plan"
                         :segments [{:type "Segment"
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
                                                  {:type "Product-component"
                                                   :name {:type "Cell"
                                                          :name "lacing"}
                                                   :children [{:type "Relationship"
                                                               :relationshipType "consequence"
                                                               :children [{:type "Quote"
                                                                           :text "a snug fit for everyday wear"}]}]}]}]}

          data {:product-name "Nike Air"
                :main-feature "comfort"
                :secondary-feature "support"
                :other-feature "style"
                :lacing "premium lacing"
                :style "with sleek update on a classic design"}
          result (render-dp document-plan data false)
          expected-any ["Nike Air provides comfort, support and style with sleek update on a classic design. Premium lacing results in a snug fit for everyday wear."
                        "Nike Air provides comfort, support and style with sleek update on a classic design. Premium lacing results in a situation where laces never gets into a knot."]]
      (println "Result: " result)
      (is (some #(= % result) expected-any))))
  
  (testing "Document plan with `if-then-else` expression without else statement missmatch"
    (let [document-plan {:type "Document-plan"
                         :segments [{:type "Segment"
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
                                                  {:type "If-then-else"
                                                   :conditions [{:type "If-condition"
                                                                 :condition {:type "Value-comparison"
                                                                             :operator "="
                                                                             :value1 {:type "Cell"
                                                                                      :name "lacing"}
                                                                             :value2 {:type "Quote"
                                                                                      :text "Premium lacing"}}
                                                                 :thenExpression {:type "Product-component"
                                                                                  :name {:type "Cell"
                                                                                         :name "lacing"}
                                                                                  :children [{:type "Relationship"
                                                                                              :relationshipType "consequence"
                                                                                              :children [{:type "Quote"
                                                                                                          :text "a snug fit for everyday wear"}]
                                                                                              }]                
                                                                                  }}
                                                                ]}]}]}

          data {:product-name "Nike Air"
                :main-feature "comfort"
                :secondary-feature "support"
                :other-feature "style"
                :lacing "premium lacing"
                :style "with sleek update on a classic design"}
          result (render-dp document-plan data false)
          expected "Nike Air provides comfort, support and style with sleek update on a classic design."]
      (println "Result: " result)
      (is (= result expected))))
  (testing "Document plan with `if-then-else` expression without else statement match"
    (let [document-plan {:type "Document-plan"
                         :segments [{:type "Segment"
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
                                                  {:type "If-then-else"
                                                   :conditions [{:type "If-condition"
                                                                 :condition {:type "Value-comparison"
                                                                             :operator "="
                                                                             :value1 {:type "Cell"
                                                                                      :name "lacing"}
                                                                             :value2 {:type "Quote"
                                                                                      :text "premium lacing"}}
                                                                 :thenExpression {:type "Product-component"
                                                                                  :name {:type "Cell"
                                                                                         :name "lacing"}
                                                                                  :children [{:type "Relationship"
                                                                                              :relationshipType "consequence"
                                                                                              :children [{:type "Quote"
                                                                                                          :text "a snug fit for everyday wear"}]
                                                                                              }]                
                                                                                  }}
                                                                ]}]}]}

          data {:product-name "Nike Air"
                :main-feature "comfort"
                :secondary-feature "support"
                :other-feature "style"
                :lacing "premium lacing"
                :style "with sleek update on a classic design"}
          result (render-dp document-plan data false)
          expected "Nike Air provides comfort, support and style with sleek update on a classic design. Premium lacing results in a snug fit for everyday wear."]
      (println "Result: " result)
      (is (= result expected))))
  (testing "Document plan with 'lexicon'"
    (let [document-plan {:type "Document-plan"
                         :segments [{:type "Segment"
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
                                                                        {:type "Lexicon"
                                                                         :text "style"}]}
                                                            {:type "Rhetorical"
                                                             :rstType "elaboration"
                                                             :children [{:type "Cell"
                                                                         :name "style"}]
                                                             }]}
                                                {:type "If-then-else"
                                                 :conditions [{:type "If-condition"
                                                               :condition {:type "Value-comparison"
                                                                           :operator "="
                                                                           :value1 {:type "Cell"
                                                                                    :name "lacing"}
                                                                           :value2 {:type "Quote"
                                                                                    :text "Premium lacing"}}
                                                               :thenExpression {:type "Product-component"
                                                                                :name {:type "Cell"
                                                                                       :name "lacing"}
                                                                                :children [{:type "Relationship"
                                                                                            :relationshipType "consequence"
                                                                                            :children [{:type "Quote"
                                                                                                        :text "a snug fit for everyday wear"}]
                                                                                            }]
                                                                                }}
                                                              ]}]}]}

          data {:product-name "Nike Air"
                :main-feature "comfort"
                :secondary-feature "support"
                :lacing "premium lacing"
                :style "with sleek update on a classic design"}
          result (render-dp document-plan data false)
          expected #{"Nike Air provides comfort, support and style with sleek update on a classic design."
                     "Nike Air provides comfort, support and elegance with sleek update on a classic design."
                     "Nike Air provides comfort, support and grace with sleek update on a classic design."
                     "Nike Air provides comfort, support and flair with sleek update on a classic design."
                     "Nike Air provides comfort, support and title with sleek update on a classic design."}]
      (println "Result: " result)
      (is (contains? expected result)))))
