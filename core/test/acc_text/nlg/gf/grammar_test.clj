(ns acc-text.nlg.gf.grammar-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build)

(defn build-grammar [semantic-graph-name context]
  (grammar/build "Default" "Instance" (utils/load-test-semantic-graph semantic-graph-name) context))

(deftest gf-grammar-building
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params ["Amr03"]
                                 :body   [{:type  :function
                                           :value "Amr03"}]
                                 :ret    [:s "Str"]}
                                {:name   "Amr03"
                                 :params []
                                 :body   [[{:type   :operation
                                            :params [{:type  :variable
                                                      :value "DictionaryItem04"}
                                                     {:type  :variable
                                                      :value "Quote05"}
                                                     {:type  :variable
                                                      :value "Quote06"}]
                                            :value  "atLocation"}]]
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "DictionaryItem04"
                                 :type  "N"
                                 :value ["arena" "place" "venue"]}
                                {:name  "Quote05"
                                 :type  "N"
                                 :value ["city centre"]}
                                {:name  "Quote06"
                                 :type  "N"
                                 :value ["Alimentum"]}]}
         (build-grammar
           "location-amr"
           {:amr        {:at-location {:frames [{:syntax [{:type  :gf
                                                           :value "atLocation"
                                                           :roles ["lexicon" "objectRef" "locationData"]
                                                           :ret   ["N" "N" "N"]}]}]}}
            :dictionary {"place" ["arena" "place" "venue"]}}))))

(deftest grammar-building
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params []
                                 :body   [{:type  :variable
                                           :value "Data03"}]
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "Data03"
                                 :type  "Str"
                                 :value ["product"]}]}
         (build-grammar "simple-plan" {:data {:product-name "product"}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params []
                                 :body   [{:type  :variable
                                           :value "Quote03"}]
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "Quote03"
                                 :type  "Str"
                                 :value ["this is a very good book: Building Search Applications"]}]}
         (build-grammar "single-quote" {:data {:title "Building Search Applications"}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params ["Modifier03"]
                                 :body   [{:type  :function
                                           :value "Modifier03"}]
                                 :ret    [:s "Str"]}
                                {:name   "Modifier03"
                                 :params []
                                 :body   [{:type  :variable
                                           :value "DictionaryItem05"}
                                          {:type  :variable
                                           :value "Data04"}]
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "Data04"
                                 :type  "Str"
                                 :value ["Building Search Applications"]}
                                {:name  "DictionaryItem05"
                                 :type  "Str"
                                 :value ["good"]}]}
         (build-grammar "adjective-phrase" {:data {:title "Building Search Applications"}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params ["Amr03"]
                                 :body   [{:type  :function
                                           :value "Amr03"}]
                                 :ret    [:s "Str"]}
                                {:name   "Amr03"
                                 :params []
                                 :body   [[{:type  :literal
                                            :value "{{co-Agent}}"}
                                           {:type  :literal
                                            :value "is"}
                                           {:selectors {:number :singular
                                                        :tense  :past}
                                            :type      :literal
                                            :value     "{{...}}"}
                                           {:type  :literal
                                            :value "by"}
                                           {:type  :literal
                                            :value "{{Agent}}"}]]
                                 :ret    [:s "Str"]}]
                    :variables []}
         (build-grammar
           "author-amr"
           {:amr        {:author {:frames [{:syntax [{:pos :NP :role "co-Agent"} {:pos :LEX :value "is"}
                                                     {:pos :VERB :tense :past :number :singular}
                                                     {:pos :ADP :value "by"}
                                                     {:pos :NP :role "Agent"}]}]}}
            :dictionary {"good"    ["excellent"]
                         "written" ["authored"]}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params ["Amr03"]
                                 :body   [{:type  :function
                                           :value "Amr03"}]
                                 :ret    [:s "Str"]}
                                {:name   "Amr03"
                                 :params ["Modifier05"]
                                 :body   [[{:type  :function
                                            :value "Modifier05"}
                                           {:type  :literal
                                            :value "is"}
                                           {:type  :literal
                                            :value "the author of"}
                                           {:type  :variable
                                            :value "Data08"}]
                                          [{:type  :variable
                                            :value "Data08"}
                                           {:type  :literal
                                            :value "is"}
                                           {:type  :variable
                                            :value "DictionaryItem04"}
                                           {:type  :literal
                                            :value "by"}
                                           {:type  :function
                                            :value "Modifier05"}]]
                                 :ret    [:s "Str"]}
                                {:name   "Modifier05"
                                 :params []
                                 :body   [{:type  :variable
                                           :value "DictionaryItem07"}
                                          {:type  :variable
                                           :value "Data06"}]
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "DictionaryItem04"
                                 :type  "Str"
                                 :value ["written"]}
                                {:name  "Data06"
                                 :type  "Str"
                                 :value ["Manu Konchady"]}
                                {:name  "DictionaryItem07"
                                 :type  "Str"
                                 :value ["good"]}
                                {:name  "Data08"
                                 :type  "Str"
                                 :value ["Building Search Applications"]}]}
         (build-grammar
           "author-amr-with-adj"
           {:amr  {:author {:frames [{:syntax [{:pos :NP :role "Agent"}
                                               {:pos :LEX :value "is"}
                                               {:pos :LEX :value "the author of"}
                                               {:pos :NP :role "co-Agent"}]}
                                     {:syntax [{:pos :NP :role "co-Agent"}
                                               {:pos :LEX :value "is"}
                                               {:pos :VERB :role "lexicon"}
                                               {:pos :ADP :value "by"}
                                               {:pos :NP :role "Agent"}]}]}}
            :data {:authors "Manu Konchady"
                   :title   "Building Search Applications"}})))
  (is (= #::grammar{:instance  "Instance"
                    :module    "Default"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment02"]
                                 :body   [{:type  :function
                                           :value "Segment02"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment02"
                                 :params ["Sequence03"]
                                 :body   [{:type  :function
                                           :value "Sequence03"}]
                                 :ret    [:s "Str"]}
                                {:name   "Sequence03"
                                 :params ["Shuffle05"]
                                 :body   [{:type  :variable
                                           :value "DictionaryItem04"}
                                          {:type  :function
                                           :value "Shuffle05"}]
                                 :ret    [:s "Str"]}
                                {:name   "Shuffle05"
                                 :params []
                                 :body   []
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "DictionaryItem04"
                                 :type  "Str"
                                 :value ["1"]}]}
         (build-grammar "sequence-with-empty-shuffle" {})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
                                 :params ["Segment04"]
                                 :body   [{:type  :function
                                           :value "Segment04"}]
                                 :ret    [:s "Str"]}
                                {:name   "Variable02"
                                 :params []
                                 :body   [{:type  :variable
                                           :value "Quote03"}]
                                 :ret    [:s "Str"]}
                                {:name   "Segment04"
                                 :params ["Reference05"]
                                 :body   [{:type  :function
                                           :value "Reference05"}]
                                 :ret    [:s "Str"]}
                                {:name   "Reference05"
                                 :params ["Variable02"]
                                 :body   [[{:type  :function
                                            :value "Variable02"}]]
                                 :ret    [:s "Str"]}]
                    :variables [{:name  "Quote03"
                                 :type  "Str"
                                 :value ["some text"]}]}
         (build-grammar "variable" {}))))
