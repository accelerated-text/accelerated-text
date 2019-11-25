(ns acc-text.nlg.gf.grammar-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.gf.grammar.function :as function]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build `function/build)

(defn build-grammar [semantic-graph-name context]
  (grammar/build :module :instance (utils/load-test-semantic-graph semantic-graph-name) context))

(deftest ^:integration grammar-building
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [#::function{:name "DocumentPlan01"
                                           :args ["Segment02"]
                                           :body [{:type :function :value "Segment02"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Segment02"
                                           :args ["Data03"]
                                           :body [{:type :function :value "Data03"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Data03"
                                           :args []
                                           :body [{:type :literal :value "{{product-name}}"}]
                                           :ret  [:s "Str"]}]}
         (build-grammar "simple-plan" {})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [#::function{:name "DocumentPlan01"
                                           :args ["Segment02"]
                                           :body [{:type :function :value "Segment02"}]
                                           :ret  [:s "Str"]}
                               #::function{:args ["Quote03"]
                                           :body [{:type :function :value "Quote03"}]
                                           :name "Segment02"
                                           :ret  [:s "Str"]}
                               #::function{:args []
                                           :body [{:type :literal :value "this is a very good book: {{TITLE}}"}]
                                           :name "Quote03"
                                           :ret  [:s "Str"]}]}
         (build-grammar "single-quote" {})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [#::function{:name "DocumentPlan01"
                                           :args ["Segment02"]
                                           :body [{:type :function :value "Segment02"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Segment02"
                                           :args ["Data03"]
                                           :body [{:type :function :value "Data03"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Data03"
                                           :args ["DictionaryItem04"]
                                           :body [{:type :function :value "DictionaryItem04"}
                                                  {:type :operator :value "++"}
                                                  {:type :literal :value "{{title}}"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "DictionaryItem04"
                                           :args []
                                           :body [{:type :literal :value "excellent"}
                                                  {:type :operator :value "|"}
                                                  {:type :literal :value "good"}]
                                           :ret  [:s "Str"]}]}
         (build-grammar "adjective-phrase" {:dictionary {"good" ["excellent"]}})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [#::function{:name "DocumentPlan01"
                                           :args ["Segment02"]
                                           :body [{:type :function :value "Segment02"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Segment02"
                                           :args ["Amr03"]
                                           :body [{:type :function :value "Amr03"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Amr03"
                                           :args ["DictionaryItem04"
                                                  "Data05"
                                                  "Data07"]
                                           :body [{:type :operator :value "("}
                                                  {:type :function :value "Data05"}
                                                  {:type :operator :value "++"}
                                                  {:type :literal :value "is"}
                                                  {:type :operator :value "++"}
                                                  {:type :literal :value "the author of"}
                                                  {:type :operator :value "++"}
                                                  {:type :function :value "Data07"}
                                                  {:type :operator :value ")"}
                                                  {:type :operator :value "|"}
                                                  {:type :operator :value "("}
                                                  {:type :function :value "Data07"}
                                                  {:type :operator :value "++"}
                                                  {:type :literal :value "is"}
                                                  {:type :operator :value "++"}
                                                  {:type :function :value "DictionaryItem04"}
                                                  {:type :operator :value "++"}
                                                  {:type :literal :value "by"}
                                                  {:type :operator :value "++"}
                                                  {:type :function :value "Data05"}
                                                  {:type :operator :value ")"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "DictionaryItem04"
                                           :args []
                                           :body [{:type :literal :value "authored"}
                                                  {:type :operator :value "|"}
                                                  {:type :literal :value "written"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Data05"
                                           :args ["DictionaryItem06"]
                                           :body [{:type :function :value "DictionaryItem06"}
                                                  {:type :operator :value "++"}
                                                  {:type :literal :value "{{authors}}"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "DictionaryItem06"
                                           :args []
                                           :body [{:type :literal :value "excellent"}
                                                  {:type :operator :value "|"}
                                                  {:type :literal :value "good"}]
                                           :ret  [:s "Str"]}
                               #::function{:name "Data07"
                                           :args []
                                           :body [{:type :literal :value "{{title}}"}]
                                           :ret  [:s "Str"]}]}
         (build-grammar
           "author-amr-with-adj"
           {:amr        {:author {:frames [{:syntax [{:pos :NP :value "Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :LEX :value "the author of"}
                                                     {:pos :NP :value "co-Agent"}]}
                                           {:syntax [{:pos :NP :value "co-Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :VERB}
                                                     {:pos :PREP :value "by"}
                                                     {:pos :NP :value "Agent"}]}]}}
            :dictionary {"good"    ["excellent"]
                         "written" ["authored"]}}))))
