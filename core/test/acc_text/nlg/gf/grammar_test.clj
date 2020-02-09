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
                    :variables [{:name  "DictionaryItem04"
                                 :type  "N"
                                 :value ["arena" "place" "venue"]}
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
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
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
                    :variables [{:name  "Data03"
                                 :type  "Str"
                                 :value ["product"]}]}
         (build-grammar "simple-plan" {:data {:product-name "product"}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
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
                    :variables [{:name  "Quote03"
                                 :type  "Str"
                                 :value ["this is a very good book: Building Search Applications"]}]}
         (build-grammar "single-quote" {:data {:title "Building Search Applications"}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
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
                                 :body   [[{:kind  :literal
                                            :value "{{co-Agent}}"}
                                           {:kind  :literal
                                            :value "is"}
                                           {:selectors {:number :singular
                                                        :tense  :past}
                                            :kind      :literal
                                            :value     "{{...}}"}
                                           {:kind  :literal
                                            :value "by"}
                                           {:kind  :literal
                                            :value "{{Agent}}"}]]
                                 :ret    [:s "Str"]}]
                    :variables []}
         (build-grammar
           "author-amr"
           {:amr        {"author" {:frames [{:syntax [{:pos :NP :role "co-Agent"} {:pos :LEX :value "is"}
                                                      {:pos :VERB :tense :past :number :singular}
                                                      {:pos :ADP :value "by"}
                                                      {:pos :NP :role "Agent"}]}]}}
            :dictionary {"good"    ["excellent"]
                         "written" ["authored"]}})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
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
  (is (= #::grammar{:instance  "Instance"
                    :module    "Default"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
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
                    :variables [{:name  "DictionaryItem04"
                                 :type  "Str"
                                 :value ["1"]}]}
         (build-grammar "sequence-with-empty-shuffle" {})))
  (is (= #::grammar{:module    "Default"
                    :instance  "Instance"
                    :flags     {:startcat "DocumentPlan01"}
                    :functions [{:name   "DocumentPlan01"
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
                    :variables [{:name  "Quote03"
                                 :type  "Str"
                                 :value ["some text"]}]}
         (build-grammar "variable" {})))
  (is (= #::grammar{:flags     {:startcat "DocumentPlan01"}
                    :functions [{:body   [{:kind  :function
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
                    :instance  "Instance"
                    :module    "Default"
                    :variables [{:name  "Quote05"
                                 :type  "N"
                                 :value ["refrigerator"]}
                                {:name  "DictionaryItem06"
                                 :type  "A"
                                 :value ["old" "outdated"]}
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
                                                        :type   :oper}]}]}}})))
  (is (= '#:acc-text.nlg.gf.grammar{:flags     {:startcat "DocumentPlan01"}
                                    :functions ({:body   ({:kind  :function
                                                           :value "Segment02"})
                                                 :name   "DocumentPlan01"
                                                 :params ("Segment02")
                                                 :ret    [:s
                                                          "Str"]
                                                 :type   :document-plan}
                                                {:body   ({:kind  :function
                                                           :value "Amr03"})
                                                 :name   "Segment02"
                                                 :params ("Amr03")
                                                 :ret    [:s
                                                          "Str"]
                                                 :type   :segment}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr04"})
                                                            :value  "mkS"}))
                                                 :name   "Amr03"
                                                 :params ("Amr04")
                                                 :ret    [:s
                                                          "S"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr05"}
                                                                     {:kind  :function
                                                                      :value "Amr08"})
                                                            :value  "mkCl"}))
                                                 :name   "Amr04"
                                                 :params ("Amr05"
                                                           "Amr08")
                                                 :ret    [:s
                                                          "Cl"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr06"})
                                                            :value  "mkNP"}))
                                                 :name   "Amr05"
                                                 :params ("Amr06")
                                                 :ret    [:s
                                                          "NP"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :variable
                                                                      :value "Quote07"})
                                                            :value  "mkN"}))
                                                 :name   "Amr06"
                                                 :params ()
                                                 :ret    [:s
                                                          "N"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr09"}
                                                                     {:kind  :function
                                                                      :value "Amr13"})
                                                            :value  "mkVP"}))
                                                 :name   "Amr08"
                                                 :params ("Amr09"
                                                           "Amr13")
                                                 :ret    [:s
                                                          "VP"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr10"})
                                                            :value  "passiveVP"}))
                                                 :name   "Amr09"
                                                 :params ("Amr10")
                                                 :ret    [:s
                                                          "VP"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr11"})
                                                            :value  "mkV2"}))
                                                 :name   "Amr10"
                                                 :params ("Amr11")
                                                 :ret    [:s
                                                          "V2"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :variable
                                                                      :value "Quote12"})
                                                            :value  "mkV"}))
                                                 :name   "Amr11"
                                                 :params ()
                                                 :ret    [:s
                                                          "V"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr14"}
                                                                     {:kind  :function
                                                                      :value "Amr16"})
                                                            :value  "mkAdv"}))
                                                 :name   "Amr13"
                                                 :params ("Amr14"
                                                           "Amr16")
                                                 :ret    [:s
                                                          "Adv"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :variable
                                                                      :value "Quote15"})
                                                            :value  "mkPrep"}))
                                                 :name   "Amr14"
                                                 :params ()
                                                 :ret    [:s
                                                          "Prep"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :function
                                                                      :value "Amr17"})
                                                            :value  "mkNP"}))
                                                 :name   "Amr16"
                                                 :params ("Amr17")
                                                 :ret    [:s
                                                          "NP"]
                                                 :type   :amr}
                                                {:body   (({:kind   :operation
                                                            :params ({:kind  :variable
                                                                      :value "Quote18"})
                                                            :value  "mkN"}))
                                                 :name   "Amr17"
                                                 :params ()
                                                 :ret    [:s
                                                          "N"]
                                                 :type   :amr})
                                    :instance  "Instance"
                                    :module    "Default"
                                    :variables ({:name  "Quote07"
                                                 :type  "Str"
                                                 :value ["refrigerator"]}
                                                {:name  "Quote12"
                                                 :type  "Str"
                                                 :value ["produce"]}
                                                {:name  "Quote15"
                                                 :type  "Str"
                                                 :value ["of"]}
                                                {:name  "Quote18"
                                                 :type  "Str"
                                                 :value ["steel"]})}
         (build-grammar
           "rgl-quote"
           {:dictionary {}
            :amr        {"mkN/Str->N"                       {:id     "mkN/Str->N"
                                                             :kind   "N"
                                                             :roles  [{:type "Str"}]
                                                             :label  "mkN"
                                                             :name   "Str -> N"
                                                             :frames [{:syntax [{:type :oper :value "mkN" :ret "N" :params [{:type "Str"}]}]}]}
                         "mkNP/N->NP"                       {:id     "mkNP/N->NP"
                                                             :kind   "NP"
                                                             :roles  [{:type "N"}]
                                                             :label  "mkNP"
                                                             :name   "N -> NP"
                                                             :frames [{:syntax [{:type :oper :value "mkNP" :ret "NP" :params [{:type "N"}]}]}]}
                         "mkCl/NP->VP->Cl"                  {:id     "mkCl/NP->VP->Cl"
                                                             :kind   "Cl"
                                                             :roles  [{:type "NP"} {:type "VP"}]
                                                             :label  "mkCl"
                                                             :name   "NP -> VP -> Cl"
                                                             :frames [{:syntax [{:type   :oper
                                                                                 :value  "mkCl"
                                                                                 :ret    "Cl"
                                                                                 :params [{:type "NP"} {:type "VP"}]}]}]}
                         "mkVP/VP->Adv->VP"                 {:id     "mkVP/VP->Adv->VP"
                                                             :kind   "VP"
                                                             :roles  [{:type "VP"} {:type "Adv"}]
                                                             :label  "mkVP"
                                                             :name   "VP -> Adv -> VP"
                                                             :frames [{:syntax [{:type   :oper
                                                                                 :value  "mkVP"
                                                                                 :ret    "VP"
                                                                                 :params [{:type "VP"} {:type "Adv"}]}]}]}
                         "mkPrep/Str->Prep"                 {:id     "mkPrep/Str->Prep"
                                                             :kind   "Prep"
                                                             :roles  [{:type "Str"}]
                                                             :label  "mkPrep"
                                                             :name   "Str -> Prep"
                                                             :frames [{:syntax [{:type :oper :value "mkPrep" :ret "Prep" :params [{:type "Str"}]}]}]}
                         "mkS/(Tense)->(Ant)->(Pol)->Cl->S" {:id     "mkS/(Tense)->(Ant)->(Pol)->Cl->S"
                                                             :kind   "S"
                                                             :roles  [{:type "(Tense)"} {:type "(Ant)"} {:type "(Pol)"} {:type "Cl"}]
                                                             :label  "mkS"
                                                             :name   "(Tense) -> (Ant) -> (Pol) -> Cl -> S"
                                                             :frames [{:syntax [{:type   :oper
                                                                                 :value  "mkS"
                                                                                 :ret    "S"
                                                                                 :params [{:type "(Tense)"}
                                                                                          {:type "(Ant)"}
                                                                                          {:type "(Pol)"}
                                                                                          {:type "Cl"}]}]}]}
                         "mkV2/V->V2"                       {:id     "mkV2/V->V2"
                                                             :kind   "V2"
                                                             :roles  [{:type "V"}]
                                                             :label  "mkV2"
                                                             :name   "V -> V2"
                                                             :frames [{:syntax [{:type :oper :value "mkV2" :ret "V2" :params [{:type "V"}]}]}]}
                         "mkAdv/Prep->NP->Adv"              {:id     "mkAdv/Prep->NP->Adv"
                                                             :kind   "Adv"
                                                             :roles  [{:type "Prep"} {:type "NP"}]
                                                             :label  "mkAdv"
                                                             :name   "Prep -> NP -> Adv"
                                                             :frames [{:syntax [{:type   :oper
                                                                                 :value  "mkAdv"
                                                                                 :ret    "Adv"
                                                                                 :params [{:type "Prep"} {:type "NP"}]}]}]}
                         "passiveVP/V2->VP"                 {:id     "passiveVP/V2->VP"
                                                             :kind   "VP"
                                                             :roles  [{:type "V2"}]
                                                             :label  "passiveVP"
                                                             :name   "V2 -> VP"
                                                             :frames [{:syntax [{:type :oper :value "passiveVP" :ret "VP" :params [{:type "V2"}]}]}]}
                         "mkV/Str->V"                       {:id     "mkV/Str->V"
                                                             :kind   "V"
                                                             :roles  [{:type "Str"}]
                                                             :label  "mkV"
                                                             :name   "Str -> V"
                                                             :frames [{:syntax [{:type :oper :value "mkV" :ret "V" :params [{:type "Str"}]}]}]}}}))))
