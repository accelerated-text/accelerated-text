(ns acc-text.nlg.gf.grammar-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build)

(defn build-grammar [semantic-graph-name context]
  (grammar/build :module :instance (utils/load-test-semantic-graph semantic-graph-name) context))

(deftest grammar-building
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:name   "DocumentPlan01"
                                :params ["Segment02"]
                                :body   [{:type :function :value "Segment02"}]
                                :ret    [:s "Str"]}
                               {:name   "Segment02"
                                :params ["Data03"]
                                :body   [{:type :function :value "Data03"}]
                                :ret    [:s "Str"]}
                               {:name   "Data03"
                                :params []
                                :body   [{:type :literal :value "{{product-name}}"}]
                                :ret    [:s "Str"]}]}
         (build-grammar "simple-plan" {})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:name   "DocumentPlan01"
                                :params ["Segment02"]
                                :body   [{:type :function :value "Segment02"}]
                                :ret    [:s "Str"]}
                               {:params ["Quote03"]
                                :body   [{:type :function :value "Quote03"}]
                                :name   "Segment02"
                                :ret    [:s "Str"]}
                               {:params []
                                :body   [{:type :literal :value "this is a very good book: {{TITLE}}"}]
                                :name   "Quote03"
                                :ret    [:s "Str"]}]}
         (build-grammar "single-quote" {})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:name   "DocumentPlan01"
                                :params ["Segment02"]
                                :body   [{:type :function :value "Segment02"}]
                                :ret    [:s "Str"]}
                               {:name   "Segment02"
                                :params ["Data03"]
                                :body   [{:type :function :value "Data03"}]
                                :ret    [:s "Str"]}
                               {:name   "Data03"
                                :params ["DictionaryItem04"]
                                :body   [{:type :function :value "DictionaryItem04"}
                                         {:type :operator :value "++"}
                                         {:type :literal :value "{{title}}"}]
                                :ret    [:s "Str"]}
                               {:name   "DictionaryItem04"
                                :params []
                                :body   [{:type :literal :value "excellent"}
                                         {:type :operator :value "|"}
                                         {:type :literal :value "good"}]
                                :ret    [:s "Str"]}]}
         (build-grammar "adjective-phrase" {:dictionary {"good" ["excellent"]}})))
  (is (= #::grammar{:flags    {:startcat "DocumentPlan01"}
                    :instance :instance
                    :module   :module
                    :syntax   [{:name   "DocumentPlan01"
                                :params ["Segment02"]
                                :body   [{:type :function :value "Segment02"}]
                                :ret    [:s "Str"]}
                               {:name   "Segment02"
                                :params ["Amr03"]
                                :body   [{:type :function :value "Amr03"}]
                                :ret    [:s "Str"]}
                               {:name   "Amr03"
                                :params []
                                :body   [{:pos :NP :type :literal :value "{{co-Agent}}"}
                                         {:type :operator :value "++"}
                                         {:pos :LEX :type :literal :value "is"}
                                         {:type :operator :value "++"}
                                         {:pos :VERB :type :literal :value "{{...}}" :selectors {:number :singular :tense :past}}
                                         {:type :operator :value "++"}
                                         {:pos :ADP :type :literal :value "by"}
                                         {:type :operator :value "++"}
                                         {:pos :NP :type :literal :value "{{Agent}}"}]
                                :ret    [:s "Str"]}]}
         (build-grammar
           "author-amr"
           {:amr        {:author {:frames [{:syntax [{:pos :NP :role "co-Agent"} {:pos :LEX :value "is"}
                                                     {:pos :VERB :tense :past :number :singular}
                                                     {:pos :ADP :value "by"}
                                                     {:pos :NP :role "Agent"}]}]}}
            :dictionary {"good"    ["excellent"]
                         "written" ["authored"]}})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:name   "DocumentPlan01"
                                :params ["Segment02"]
                                :body   [{:type :function :value "Segment02"}]
                                :ret    [:s "Str"]}
                               {:name   "Segment02"
                                :params ["Amr03"]
                                :body   [{:type :function :value "Amr03"}]
                                :ret    [:s "Str"]}
                               {:name   "Amr03"
                                :params ["DictionaryItem04"
                                         "Data05"
                                         "Data07"]
                                :body   [{:type :operator :value "("}
                                         {:pos :NP :type :function :value "Data05"}
                                         {:type :operator :value "++"}
                                         {:pos :LEX :type :literal :value "is"}
                                         {:type :operator :value "++"}
                                         {:pos :LEX :type :literal :value "the author of"}
                                         {:type :operator :value "++"}
                                         {:pos :NP :type :function :value "Data07"}
                                         {:type :operator :value ")"}
                                         {:type :operator :value "|"}
                                         {:type :operator :value "("}
                                         {:pos :NP :type :function :value "Data07"}
                                         {:type :operator :value "++"}
                                         {:pos :LEX :type :literal :value "is"}
                                         {:type :operator :value "++"}
                                         {:pos :VERB :type :function :value "DictionaryItem04"}
                                         {:type :operator :value "++"}
                                         {:pos :ADP :type :literal :value "by"}
                                         {:type :operator :value "++"}
                                         {:pos :NP :type :function :value "Data05"}
                                         {:type :operator :value ")"}]
                                :ret    [:s "Str"]}
                               {:name   "DictionaryItem04"
                                :params []
                                :body   [{:type :literal :value "authored"}
                                         {:type :operator :value "|"}
                                         {:type :literal :value "written"}]
                                :ret    [:s "Str"]}
                               {:name   "Data05"
                                :params ["DictionaryItem06"]
                                :body   [{:type :function :value "DictionaryItem06"}
                                         {:type :operator :value "++"}
                                         {:type :literal :value "{{authors}}"}]
                                :ret    [:s "Str"]}
                               {:name   "DictionaryItem06"
                                :params []
                                :body   [{:type :literal :value "excellent"}
                                         {:type :operator :value "|"}
                                         {:type :literal :value "good"}]
                                :ret    [:s "Str"]}
                               {:name   "Data07"
                                :params []
                                :body   [{:type :literal :value "{{title}}"}]
                                :ret    [:s "Str"]}]}
         (build-grammar
           "author-amr-with-adj"
           {:amr        {:author {:frames [{:syntax [{:pos :NP :role "Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :LEX :value "the author of"}
                                                     {:pos :NP :role "co-Agent"}]}
                                           {:syntax [{:pos :NP :role "co-Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :VERB}
                                                     {:pos :ADP :value "by"}
                                                     {:pos :NP :role "Agent"}]}]}}
            :dictionary {"good"    ["excellent"]
                         "written" ["authored"]}})))
  (is (= #::grammar{:flags    {:startcat "DocumentPlan01"}
                    :instance :instance
                    :module   :module
                    :syntax   [{:name   "DocumentPlan01"
                                :params ["Segment02"]
                                :body   [{:type :function :value "Segment02"}]
                                :ret    [:s "Str"]}
                               {:name   "Segment02"
                                :params ["Sequence03"]
                                :body   [{:type :function :value "Sequence03"}]
                                :ret    [:s "Str"]}
                               {:name   "Sequence03"
                                :params ["DictionaryItem04"
                                         "Shuffle05"]
                                :body   [{:type :function :value "DictionaryItem04"}
                                         {:type :operator :value "++"}
                                         {:type :function :value "Shuffle05"}]
                                :ret    [:s "Str"]}
                               {:name   "DictionaryItem04"
                                :params []
                                :body   [{:type :literal :value "1"}]
                                :ret    [:s "Str"]}
                               {:name   "Shuffle05"
                                :params []
                                :body   []
                                :ret    [:s "Str"]}]}
         (build-grammar "sequence-with-empty-shuffle" {}))))
