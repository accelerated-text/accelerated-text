(ns acc-text.nlg.semantic-graph-test
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.test :refer [deftest is testing]]))

(deftest instance-building
  (let [semantic-graph #::sg{:relations [#::sg{:from :01 :to :02 :role :segment}
                                         #::sg{:from :02 :to :03 :role :instance}
                                         #::sg{:from :03 :to :04 :role :function}
                                         #::sg{:from :03 :to :05 :role :ARG0 :attributes #::sg{:name "agent"}}
                                         #::sg{:from :03 :to :07 :role :ARG1 :attributes #::sg{:name "co-agent"}}
                                         #::sg{:from :05 :to :06 :role :modifier}]
                             :concepts  [#::sg{:id :01 :type :document-plan}
                                         #::sg{:id :02 :type :segment}
                                         #::sg{:id :03 :type :amr :value "author"}
                                         #::sg{:id :04 :type :dictionary-item :value "written" :attributes #::sg{:name "written"}}
                                         #::sg{:id :05 :type :data :value "authors"}
                                         #::sg{:id :06 :type :dictionary-item :value "good" :attributes #::sg{:name "good"}}
                                         #::sg{:id :07 :type :data :value "title"}]}
        context #::sg{:document-plan-id "author-amr-with-adj"
                      :reader-profiles  [:default :senior]
                      :dictionary       {:default {"good"    ["excellent"]
                                                   "written" ["authored"]}
                                         :senior  {"good"    ["excellent"]
                                                   "written" ["authored"]}}
                      :amr              {:author {:id                 "author"
                                                  :dictionary-item-id "written"
                                                  :thematic-roles     [{:type "Agent"}
                                                                       {:type "co-Agent"}]
                                                  :frames             [{:examples ["X is the author of Y"]
                                                                        :syntax   [{:pos :NP :value "Agent"}
                                                                                   {:pos :LEX :value "is"}
                                                                                   {:pos :LEX :value "the author of"}
                                                                                   {:pos :NP :value "co-Agent"}]}
                                                                       {:examples ["Y is written by X"]
                                                                        :syntax   [{:pos :NP :value "co-Agent"}
                                                                                   {:pos :LEX :value "is"}
                                                                                   {:pos :VERB}
                                                                                   {:pos :PREP :value "by"}
                                                                                   {:pos :NP :value "Agent"}]}]}}}
        instances (sg/build-instances semantic-graph context)]
    (testing "Id"
      (is (= #{:author-amr-with-adj-default :author-amr-with-adj-senior} (set (map ::sg/id instances)))))
    (testing "Context"
      (let [context (map ::sg/context instances)]
        (is (= #{"author-amr-with-adj"} (set (map ::sg/document-plan-id context))))
        (is (= #{{"good" ["excellent"], "written" ["authored"]}} (set (map ::sg/dictionary context))))
        (is (= #{:default :senior} (set (map ::sg/reader-profile context))))))
    (testing "Dictionary item context adding"
      (is (= #{#::sg{:attributes #::sg{:name           "written"
                                       :reader-profile :default}
                     :id         :04
                     :members    ["authored"]
                     :type       :dictionary-item
                     :value      "written"}
               #::sg{:attributes #::sg{:name           "good"
                                       :reader-profile :default}
                     :id         :06
                     :members    ["excellent"]
                     :type       :dictionary-item
                     :value      "good"}
               #::sg{:attributes #::sg{:name           "written"
                                       :reader-profile :senior}
                     :id         :04
                     :members    ["authored"]
                     :type       :dictionary-item
                     :value      "written"}
               #::sg{:attributes #::sg{:name           "good"
                                       :reader-profile :senior}
                     :id         :06
                     :members    ["excellent"]
                     :type       :dictionary-item
                     :value      "good"}}
             (->> instances
                  (map ::sg/graph)
                  (mapcat ::sg/concepts)
                  (filter #(= (::sg/type %) :dictionary-item))
                  (set)))))))
