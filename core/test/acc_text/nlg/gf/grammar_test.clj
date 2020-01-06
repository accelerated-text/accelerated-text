(ns acc-text.nlg.gf.grammar-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build)

(defn build-grammar [semantic-graph-name context]
  (grammar/build :module :instance (utils/load-test-semantic-graph semantic-graph-name) context))

(deftest gf-grammar-building
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
                               {:name   "GF02"
                                :params []
                                :body   [{:type :gf :value "AtLocation" :params {:location "location"
                                                                                 :name "venue"}}]
                                :ret    [:s "Str"]}]}
         (build-grammar "simple-gf" {}))))

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
                    :syntax   [{:body   [{:type :function :value "Segment02"}]
                                :name   "DocumentPlan01"
                                :params ["Segment02"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "Modifier03"}]
                                :name   "Segment02"
                                :params ["Modifier03"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "DictionaryItem05"}
                                         {:type :function :value "Data04"}]
                                :name   "Modifier03"
                                :params ["Data04" "DictionaryItem05"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :literal :value "{{title}}"}]
                                :name   "Data04"
                                :params []
                                :ret    [:s "Str"]}
                               {:body   [[{:type :literal :value "good"}]]
                                :name   "DictionaryItem05"
                                :params []
                                :ret    [:s "Str"]}]}
         (build-grammar "adjective-phrase" {})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:body   [{:type :function :value "Segment02"}]
                                :name   "DocumentPlan01"
                                :params ["Segment02"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "Amr03"}]
                                :name   "Segment02"
                                :params ["Amr03"]
                                :ret    [:s "Str"]}
                               {:body   [[{:pos :NP :type :literal :value "{{co-Agent}}"}
                                          {:pos :LEX :type :literal :value "is"}
                                          {:pos       :VERB :type :literal :value "{{...}}"
                                           :selectors {:number :singular :tense :past}}
                                          {:pos :ADP :type :literal :value "by"}
                                          {:pos :NP :type :literal :value "{{Agent}}"}]]
                                :name   "Amr03"
                                :params []
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
                    :syntax   [{:body   [{:type :function :value "Segment02"}]
                                :name   "DocumentPlan01"
                                :params ["Segment02"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "Amr03"}]
                                :name   "Segment02"
                                :params ["Amr03"]
                                :ret    [:s "Str"]}
                               {:body   [[{:pos :NP :type :function :value "Modifier05"}
                                          {:pos :LEX :type :literal :value "is"}
                                          {:pos :LEX :type :literal :value "the author of"}
                                          {:pos :NP :type :function :value "Data08"}]
                                         [{:pos :NP :type :function :value "Data08"}
                                          {:pos :LEX :type :literal :value "is"}
                                          {:pos :VERB :type :function :value "DictionaryItem04"}
                                          {:pos :ADP :type :literal :value "by"}
                                          {:pos :NP :type :function :value "Modifier05"}]]
                                :name   "Amr03"
                                :params ["DictionaryItem04" "Modifier05" "Data08"]
                                :ret    [:s "Str"]}
                               {:body   [[{:type :literal :value "written"}]]
                                :name   "DictionaryItem04"
                                :params []
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "DictionaryItem07"}
                                         {:type :function :value "Data06"}]
                                :name   "Modifier05"
                                :params ["Data06" "DictionaryItem07"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :literal :value "{{authors}}"}]
                                :name   "Data06"
                                :params []
                                :ret    [:s "Str"]}
                               {:body   [[{:type :literal :value "good"}]]
                                :name   "DictionaryItem07"
                                :params []
                                :ret    [:s "Str"]}
                               {:body   [{:type :literal :value "{{title}}"}]
                                :name   "Data08"
                                :params []
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
            :dictionary {}})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:body   [{:type :function :value "Segment02"}]
                                :name   "DocumentPlan01"
                                :params ["Segment02"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "Sequence03"}]
                                :name   "Segment02"
                                :params ["Sequence03"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "DictionaryItem04"}
                                         {:type :function :value "Shuffle05"}]
                                :name   "Sequence03"
                                :params ["DictionaryItem04" "Shuffle05"]
                                :ret    [:s "Str"]}
                               {:body   [[{:type :literal :value "1"}]]
                                :name   "DictionaryItem04"
                                :params []
                                :ret    [:s "Str"]}
                               {:body   []
                                :name   "Shuffle05"
                                :params []
                                :ret    [:s "Str"]}]}
         (build-grammar "sequence-with-empty-shuffle" {})))
  (is (= #::grammar{:module   :module
                    :instance :instance
                    :flags    {:startcat "DocumentPlan01"}
                    :syntax   [{:body   [{:type :function :value "Segment04"}]
                                :name   "DocumentPlan01"
                                :params ["Segment04"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "Quote03"}]
                                :name   "Variable02"
                                :params ["Quote03"]
                                :ret    [:s "Str"]}
                               {:body   [{:type :literal :value "some text"}]
                                :name   "Quote03"
                                :params []
                                :ret    [:s "Str"]}
                               {:body   [{:type :function :value "Reference05"}]
                                :name   "Segment04"
                                :params ["Reference05"]
                                :ret    [:s "Str"]}
                               {:body   [[{:type :function :value "Variable02"}]]
                                :name   "Reference05"
                                :params ["Variable02"]
                                :ret    [:s "Str"]}]}
         (build-grammar "variable" {}))))
