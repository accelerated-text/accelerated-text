(ns acc-text.nlg.gf.grammar-test
  (:require [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build)

(defn build-grammar [semantic-graph-name context]
  (grammar/build "Default" "Instance" (utils/load-test-semantic-graph semantic-graph-name) context))

(deftest gf-grammar-building
  (is (= #::grammar{:module     "Default"
                    :instance   "Instance"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :body   [{:kind  :function
                                            :value "Segment02"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment02"
                                  :type   :segment
                                  :params ["Amr03"]
                                  :body   [{:kind  :function
                                            :value "Amr03"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Amr03"
                                  :type   :amr
                                  :params []
                                  :body   [[{:params [{:kind  :variable
                                                       :value "DictionaryItem04"}
                                                      {:kind  :variable
                                                       :value "Quote06"}
                                                      {:kind  :variable
                                                       :value "Quote05"}]
                                             :kind   :operation
                                             :value  "atLocation"}]]
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "DictionaryItem04"
                                  :type  "N"
                                  :value ["place"]}
                                 {:name  "Quote05"
                                  :type  "N"
                                  :value ["Alimentum"]}
                                 {:name  "Quote06"
                                  :type  "N"
                                  :value ["city centre"]}]}
         (build-grammar
           "location-amr"
           {:amr        {"at-location" {:frames [{:syntax [{:type   :oper
                                                            :value  "atLocation"
                                                            :ret    "S"
                                                            :params [{:role "lexicon" :type "N"}
                                                                     {:role "locationData" :type "N"}
                                                                     {:role "objectRef" :type "N"}]}]}]}}
            :dictionary {"place" ["arena" "place" "venue"]}}))))

(deftest grammar-building
  (is (= #::grammar{:module     "Default"
                    :instance   "Instance"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :body   [{:kind  :function
                                            :value "Segment02"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment02"
                                  :type   :segment
                                  :params []
                                  :body   [{:kind  :variable
                                            :value "Data03"}]
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "Data03"
                                  :type  "Str"
                                  :value ["product"]}]}
         (build-grammar "simple-plan" {:data {:product-name "product"}})))
  (is (= #::grammar{:module     "Default"
                    :instance   "Instance"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :body   [{:kind  :function
                                            :value "Segment02"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment02"
                                  :type   :segment
                                  :params []
                                  :body   [{:kind  :variable
                                            :value "Quote03"}]
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "Quote03"
                                  :type  "Str"
                                  :value ["this is a very good book: Building Search Applications"]}]}
         (build-grammar "single-quote" {:data {:title "Building Search Applications"}})))
  (is (= #::grammar{:module     "Default"
                    :instance   "Instance"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :body   [{:kind  :function
                                            :value "Segment02"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment02"
                                  :type   :segment
                                  :params ["Modifier03"]
                                  :body   [{:kind  :function
                                            :value "Modifier03"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Modifier03"
                                  :type   :modifier
                                  :params []
                                  :body   [{:kind  :variable
                                            :value "DictionaryItem05"}
                                           {:kind  :variable
                                            :value "Data04"}]
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "Data04"
                                  :type  "Str"
                                  :value ["Building Search Applications"]}
                                 {:name  "DictionaryItem05"
                                  :type  "Str"
                                  :value ["good"]}]}
         (build-grammar "adjective-phrase" {:data {:title "Building Search Applications"}})))
  (is (= #::grammar{:module     "Default"
                    :instance   "Instance"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :body   [{:kind  :function
                                            :value "Segment02"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment02"
                                  :type   :segment
                                  :params ["Amr03"]
                                  :body   [{:kind  :function
                                            :value "Amr03"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Amr03"
                                  :type   :amr
                                  :params ["Modifier05"]
                                  :body   [[{:kind  :function
                                             :value "Modifier05"}
                                            {:kind  :literal
                                             :value "is"}
                                            {:kind  :literal
                                             :value "the author of"}
                                            {:kind  :variable
                                             :value "Data08"}]
                                           [{:kind  :variable
                                             :value "Data08"}
                                            {:kind  :literal
                                             :value "is"}
                                            {:kind  :variable
                                             :value "DictionaryItem04"}
                                            {:kind  :literal
                                             :value "by"}
                                            {:kind  :function
                                             :value "Modifier05"}]]
                                  :ret    [:s "Str"]}
                                 {:name   "Modifier05"
                                  :type   :modifier
                                  :params []
                                  :body   [{:kind  :variable
                                            :value "DictionaryItem07"}
                                           {:kind  :variable
                                            :value "Data06"}]
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "DictionaryItem04"
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
           {:amr  {"author" {:frames [{:syntax [{:pos :NP :role "Agent"}
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
  (is (= #::grammar{:instance   "Instance"
                    :module     "Default"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :body   [{:kind  :function
                                            :value "Segment02"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment02"
                                  :type   :segment
                                  :params ["Sequence03"]
                                  :body   [{:kind  :function
                                            :value "Sequence03"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Sequence03"
                                  :type   :sequence
                                  :params ["Shuffle05"]
                                  :body   [{:kind  :variable
                                            :value "DictionaryItem04"}
                                           {:kind  :function
                                            :value "Shuffle05"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Shuffle05"
                                  :type   :shuffle
                                  :params []
                                  :body   []
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "DictionaryItem04"
                                  :type  "Str"
                                  :value ["1"]}]}
         (build-grammar "sequence-with-empty-shuffle" {})))
  (is (= #::grammar{:module     "Default"
                    :instance   "Instance"
                    :flags      {:startcat "DocumentPlan01"}
                    :functions  [{:name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment04"]
                                  :body   [{:kind  :function
                                            :value "Segment04"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Variable02"
                                  :type   :variable
                                  :params []
                                  :body   [{:kind  :variable
                                            :value "Quote03"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Segment04"
                                  :type   :segment
                                  :params ["Reference05"]
                                  :body   [{:kind  :function
                                            :value "Reference05"}]
                                  :ret    [:s "Str"]}
                                 {:name   "Reference05"
                                  :type   :reference
                                  :params ["Variable02"]
                                  :body   [[{:kind  :function
                                             :value "Variable02"}]]
                                  :ret    [:s "Str"]}]
                    :operations []
                    :variables  [{:name  "Quote03"
                                  :type  "Str"
                                  :value ["some text"]}]}
         (build-grammar "variable" {})))
  (is (= #::grammar{:flags      {:startcat "DocumentPlan01"}
                    :functions  [{:body   [{:kind  :function
                                            :value "Segment02"}]
                                  :name   "DocumentPlan01"
                                  :type   :document-plan
                                  :params ["Segment02"]
                                  :ret    [:s "Str"]}
                                 {:body   [{:kind  :function
                                            :value "Amr03"}]
                                  :name   "Segment02"
                                  :type   :segment
                                  :params ["Amr03"]
                                  :ret    [:s "Str"]}
                                 {:body   [[{:kind   :operation
                                             :params [{:kind  :function
                                                       :value "Modifier04"}
                                                      {:kind  :variable
                                                       :value "Quote08"}]
                                             :value  "madeOf"}]]
                                  :name   "Amr03"
                                  :type   :amr
                                  :params ["Modifier04"]
                                  :ret    [:s "Str"]}
                                 {:body   [{:kind  :variable
                                            :value "DictionaryItem06"}
                                           {:kind  :variable
                                            :value "DictionaryItem07"}
                                           {:kind  :variable
                                            :value "Quote05"}]
                                  :name   "Modifier04"
                                  :type   :modifier
                                  :params []
                                  :ret    [:s "CN"]}]
                    :instance   "Instance"
                    :module     "Default"
                    :operations []
                    :variables  [{:name  "Quote05"
                                  :type  "N"
                                  :value ["refrigerator"]}
                                 {:name  "DictionaryItem06"
                                  :type  "A"
                                  :value ["old"]}
                                 {:name  "DictionaryItem07"
                                  :type  "A"
                                  :value ["dirty"]}
                                 {:name  "Quote08"
                                  :type  "CN"
                                  :value ["steel"]}]}
         (build-grammar
           "gf-amr-modifier"
           {:dictionary {"old" ["old" "outdated"]}
            :amr        {"made-of" {:frames [{:syntax [{:ret    "S"
                                                        :value  "madeOf"
                                                        :params [{:type "CN" :role "Subject"}
                                                                 {:type "CN" :role "Object"}]
                                                        :type   :oper}]}]}}}))))

#_(deftest nested-amr-to-grammar
    (let [{variables            ::grammar/variables
           ;;ignore doc and segment parts
           [_ _ capableOf hasA] ::grammar/functions}
          (build-grammar
            "nested-amr"
            {:dictionary {"water" ["water" "H2O"]
                          "boil"  ["boil"]}
             :data       {:Make "T1000"
                          :Type "kettle"}
             :amr        {"has-a"      {:frames
                                        [{:syntax [{:ret    "NP" :value "hasA_NP"
                                                    :params [{:type "CN" :role "Subject"}
                                                             {:type "CN" :role "Object"}]
                                                    :type   :oper}]}
                                         {:syntax [{:ret    "S" :value "hasA_S"
                                                    :params [{:type "CN" :role "Subject"}
                                                             {:type "CN" :role "Object"}]
                                                    :type   :oper}]}]}
                          "capable-of" {:frames
                                        [{:syntax
                                          [{:ret    "S" :value "capableOf"
                                            :params [{:type "NP" :role "Subject"}
                                                     {:type "V2" :role "Verb"}
                                                     {:type "CN" :role "Object"}]
                                            :type   :oper}]}]}}})]
      (is (= {:name "Amr03" :type :amr :params ["Amr04"]
              :body
                    [[{:kind  :operation
                       :value "capableOf"
                       :params
                              [{:kind :function :value "Amr04"}
                               {:kind :variable :value "DictionaryItem07"}
                               {:kind :variable :value "DictionaryItem08"}]}]]
              :ret  [:s "Str"]}
             capableOf))
      (is (= {:name "Amr04" :type :amr :params []
              :body [[{:kind  :operation
                       :value "hasA_NP"
                       :params
                              [{:kind :variable :value "Data05"}
                               {:kind :variable :value "Data06"}]}]]
              :ret  "NP"}
             hasA))
      (is (= [{:name "Data05" :value ["T1000"] :type "CN"}
              {:name "Data06" :value ["kettle"] :type "CN"}
              {:name "DictionaryItem07" :value ["boil"] :type "V2"}
              {:name "DictionaryItem08" :value ["water" "H2O"] :type "CN"}]
             variables))))
