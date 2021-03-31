(ns acc-text.nlg.dictionary-test
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [acc-text.nlg.dictionary.impl :refer [resolve-dict-item]]
            [acc-text.nlg.semantic-graph.utils :refer [find-root gen-id]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.core :refer [generate-text]]
            [clojure.test :refer [deftest is are testing]]))

(defn add-ns [dict-item]
  (update dict-item ::dict-item/forms #(mapv (fn [form] #::dict-item-form{:value form}) %)))

(deftest ^:integration dictionary-item-generation
  (are [result dict-item]
    (let [dict-item-graph (resolve-dict-item (add-ns dict-item))
          document-plan (gen-id)
          segment (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    (find-root dict-item-graph)
                                                             :role  :instance
                                                             :index 0}]))]
      (is (= result (map :text (generate-text semantic-graph {} "Eng")))))

    ["Good."] #::dict-item{:key      "good_A"
                           :category "A"
                           :language "Eng"
                           :forms    ["good" "better" "best" "well"]}

    ["Absent."] #::dict-item{:key      "absent_A2"
                             :category "A2"
                             :language "Eng"
                             :forms    ["absent"]}

    ["Cat."] #::dict-item{:key        "cat_N"
                          :category   "N"
                          :language   "Eng"
                          :forms      ["cat"]
                          :attributes {"Gender" "nonhuman"}}

    ["Vilnius."] #::dict-item{:key        "Vilnius_PN"
                              :category   "PN"
                              :language   "Eng"
                              :forms      ["Vilnius"]
                              :attributes {"Gender" "nonhuman"}}

    ["Mother."] #::dict-item{:key        "mother_N2"
                             :category   "N2"
                             :language   "Eng"
                             :forms      ["mother"]
                             :attributes {"Post" "of"}}

    ["Son."] #::dict-item{:key        "son_N3"
                          :category   "N3"
                          :language   "Eng"
                          :forms      ["son"]
                          :attributes {"Prep" "from"
                                       "Post" "to"}}

    ["It."] #::dict-item{:key        "it_NP"
                         :category   "NP"
                         :language   "Eng"
                         :forms      ["it" "it" "its"]
                         :attributes {"Number" "singular"
                                      "Person" "P3"
                                      "Gender" "nonhuman"}}

    ["What."] #::dict-item{:key        "what_IP"
                           :category   "IP"
                           :language   "Eng"
                           :forms      ["what" "what" "what's"]
                           :attributes {"Number" "singular"}}

    ["Quite."] #::dict-item{:key      "quite_AdA"
                            :category "AdA"
                            :language "Eng"
                            :forms    ["quite"]}

    ["Almost."] #::dict-item{:key      "almost_AdN"
                             :category "AdN"
                             :language "Eng"
                             :forms    ["almost"]}

    ["Always."] #::dict-item{:key      "always_AdV"
                             :category "AdV"
                             :language "Eng"
                             :forms    ["always"]}

    ["Today."] #::dict-item{:key      "today_Adv"
                            :category "Adv"
                            :language "Eng"
                            :forms    ["today"]}

    ["At."] #::dict-item{:key      "at_Prep"
                         :category "Prep"
                         :language "Eng"
                         :forms    ["at"]}

    ["About."] #::dict-item{:key      "about_Post"
                            :category "Post"
                            :language "Eng"
                            :forms    ["about"]}

    ["I."] #::dict-item{:key        "I_Pron"
                        :category   "Pron"
                        :language   "Eng"
                        :forms      ["I" "me" "my" "mine"]
                        :attributes {"Number" "singular"
                                     "Person" "P1"
                                     "Gender" "human"}}

    ["Although."] #::dict-item{:key      "although_Subj"
                               :category "Subj"
                               :language "Eng"
                               :forms    ["although"]}

    ["Alas."] #::dict-item{:key      "alas_Interj"
                           :category "Interj"
                           :language "Eng"
                           :forms    ["alas"]}

    ["To feel."] #::dict-item{:key      "feel_V"
                              :category "V"
                              :language "Eng"
                              :forms    ["feel" "felt" "felt"]}

    ["To run from itself."] #::dict-item{:key        "run_V2"
                                         :category   "V2"
                                         :language   "Eng"
                                         :forms      ["run" "ran" "run"]
                                         :attributes {"Post" "from"}}))

(deftest ^:integration complex-dict-item-generation
  (testing "V3"
    (let [dict-item (add-ns #::dict-item{:key        "speak_V3"
                                         :category   "V3"
                                         :language   "Eng"
                                         :forms      ["speak" "spoke" "spoken"]
                                         :attributes {"Prep" "with"
                                                      "Post" "about"}})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg2 (gen-id)
          arg3 (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/V3->NP->NP->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg2
                                                            :type     :operation
                                                            :name     "everybody_NP"
                                                            :label    "Syntax.everybody_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}
                                                           {:id       arg3
                                                            :type     :operation
                                                            :name     "everything_NP"
                                                            :label    "Syntax.everything_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "V3"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg2
                                                             :role     :arg
                                                             :category "NP"
                                                             :index    1}
                                                            {:from     operation
                                                             :to       arg3
                                                             :role     :arg
                                                             :category "NP"
                                                             :index    2}]))]
      (is (= ["To speak with everybody about everything."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "VA"
    (let [dict-item (add-ns #::dict-item{:key      "become_VA"
                                         :category "VA"
                                         :language "Eng"
                                         :forms    ["become" "became" "become"]})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/VA->AP->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg
                                                            :type     :quote
                                                            :value    "red"
                                                            :category "Str"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "VA"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg
                                                             :role     :arg
                                                             :category "AP"
                                                             :index    1}]))]
      (is (= ["To become red."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "V2A"
    (let [dict-item (add-ns #::dict-item{:key        "paint_V2A"
                                         :category   "V2A"
                                         :language   "Eng"
                                         :forms      ["paint"]
                                         :attributes {"Prep" "above"
                                                      "Post" "with"}})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg2 (gen-id)
          arg3 (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/V2A->NP->AP->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg2
                                                            :type     :operation
                                                            :name     "it_NP"
                                                            :label    "Syntax.it_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}
                                                           {:id       arg3
                                                            :type     :quote
                                                            :value    "red"
                                                            :category "Str"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "V2A"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg2
                                                             :role     :arg
                                                             :category "NP"
                                                             :index    1}
                                                            {:from     operation
                                                             :to       arg3
                                                             :role     :arg
                                                             :category "AP"
                                                             :index    2}]))]
      (is (= ["To paint above it with red."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "VQ"
    (let [dict-item (add-ns #::dict-item{:key      "wonder_VQ"
                                         :category "VQ"
                                         :language "Eng"
                                         :forms    ["wonder" "wondered"]})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/VQ->QS->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg
                                                            :type     :operation
                                                            :name     "it_NP"
                                                            :label    "Syntax.it_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "VQ"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg
                                                             :role     :arg
                                                             :category "QS"
                                                             :index    1}]))]
      (is (= ["To wonder if there is it."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "V2Q"
    (let [dict-item (add-ns #::dict-item{:key        "ask_V2Q"
                                         :category   "V2Q"
                                         :language   "Eng"
                                         :forms      ["ask"]
                                         :attributes {"Prep" "about"}})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg2 (gen-id)
          arg3 (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/V2Q->NP->QS->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg2
                                                            :type     :operation
                                                            :name     "she_NP"
                                                            :label    "Syntax.she_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}
                                                           {:id       arg3
                                                            :type     :operation
                                                            :name     "it_NP"
                                                            :label    "Syntax.it_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "V2Q"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg2
                                                             :role     :arg
                                                             :category "NP"
                                                             :index    1}
                                                            {:from     operation
                                                             :to       arg3
                                                             :role     :arg
                                                             :category "QS"
                                                             :index    2}]))]
      (is (= ["To ask about her if there is it."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "VS"
    (let [dict-item (add-ns #::dict-item{:key      "hope_VS"
                                         :category "VS"
                                         :language "Eng"
                                         :forms    ["hope"]})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/VS->S->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg
                                                            :type     :operation
                                                            :name     "something_NP"
                                                            :label    "Syntax.something_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "VS"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg
                                                             :role     :arg
                                                             :category "S"
                                                             :index    1}]))]
      (is (= ["To hope that there is something."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "V2S"
    (let [dict-item (add-ns #::dict-item{:key        "answer_V2S"
                                         :category   "V2S"
                                         :language   "Eng"
                                         :forms      ["answer"]
                                         :attributes {"Prep" "about"}})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg2 (gen-id)
          arg3 (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/V2S->NP->S->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg2
                                                            :type     :operation
                                                            :name     "she_NP"
                                                            :label    "Syntax.she_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}
                                                           {:id       arg3
                                                            :type     :operation
                                                            :name     "it_NP"
                                                            :label    "Syntax.it_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "V2S"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg2
                                                             :role     :arg
                                                             :category "NP"
                                                             :index    1}
                                                            {:from     operation
                                                             :to       arg3
                                                             :role     :arg
                                                             :category "S"
                                                             :index    2}]))]
      (is (= ["To answer about her that there is it."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "VV"
    (let [dict-item (add-ns #::dict-item{:key      "want_VV"
                                         :category "VV"
                                         :language "Eng"
                                         :forms    ["want"]})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/VV->VP->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg
                                                            :type     :operation
                                                            :name     "have_V2"
                                                            :label    "Syntax.have_V2/V2"
                                                            :category "V2"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "VV"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg
                                                             :role     :arg
                                                             :category "VP"
                                                             :index    1}]))]
      (is (= ["To want to have itself."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "V2V"
    (let [dict-item (add-ns #::dict-item{:key        "beg_V2V"
                                         :category   "V2V"
                                         :language   "Eng"
                                         :forms      ["beg"]
                                         :attributes {"Prep" "for"
                                                      "Post" "to"}})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg2 (gen-id)
          arg3 (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkVP"
                                                            :label    "Syntax.mkVP/V2V->NP->VP->VP"
                                                            :category "VP"
                                                            :module   "Syntax"}
                                                           {:id       arg2
                                                            :type     :operation
                                                            :name     "she_NP"
                                                            :label    "Syntax.she_NP/NP"
                                                            :category "NP"
                                                            :module   "Syntax"}
                                                           {:id       arg3
                                                            :type     :operation
                                                            :name     "have_V2"
                                                            :label    "Syntax.have_V2/V2"
                                                            :category "V2"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "V2V"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg2
                                                             :role     :arg
                                                             :category "NP"
                                                             :index    1}
                                                            {:from     operation
                                                             :to       arg3
                                                             :role     :arg
                                                             :category "VP"
                                                             :index    2}]))]
      (is (= ["To beg for her to have herself."]
             (map :text (generate-text semantic-graph {} "Eng"))))))

  (testing "Conj"
    (let [dict-item (add-ns #::dict-item{:key        "and_Conj"
                                         :category   "Conj"
                                         :language   "Eng"
                                         :forms      ["and"]})
          dict-item-graph (resolve-dict-item dict-item)
          document-plan (gen-id)
          segment (gen-id)
          operation (gen-id)
          arg2 (gen-id)
          arg3 (gen-id)
          semantic-graph (-> dict-item-graph
                             (update ::sg/concepts concat [{:id   document-plan
                                                            :type :document-plan}
                                                           {:id   segment
                                                            :type :segment}
                                                           {:id       operation
                                                            :type     :operation
                                                            :name     "mkAdv"
                                                            :label    "Syntax.mkAdv/Conj->Adv->Adv->Adv"
                                                            :category "Adv"
                                                            :module   "Syntax"}
                                                           {:id       arg2
                                                            :type     :operation
                                                            :name     "here_Adv"
                                                            :label    "Syntax.here_Adv/Adv"
                                                            :category "Adv"
                                                            :module   "Syntax"}
                                                           {:id       arg3
                                                            :type     :operation
                                                            :name     "there_Adv"
                                                            :label    "Syntax.there_Adv/Adv"
                                                            :category "Adv"
                                                            :module   "Syntax"}])
                             (update ::sg/relations concat [{:from  document-plan
                                                             :to    segment
                                                             :role  :segment
                                                             :index 0}
                                                            {:from  segment
                                                             :to    operation
                                                             :role  :instance
                                                             :index 0}
                                                            {:from     operation
                                                             :to       (find-root dict-item-graph)
                                                             :role     :arg
                                                             :category "Conj"
                                                             :index    0}
                                                            {:from     operation
                                                             :to       arg2
                                                             :role     :arg
                                                             :category "Adv"
                                                             :index    1}
                                                            {:from     operation
                                                             :to       arg3
                                                             :role     :arg
                                                             :category "Adv"
                                                             :index    2}]))]
      (is (= ["Here and there."]
             (map :text (generate-text semantic-graph {} "Eng")))))))
