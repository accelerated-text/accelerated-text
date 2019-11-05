(ns acc-text.nlg.gf.realizer-test
  (:require [acc-text.nlg.gf.realizer :as sut]
            [clojure.test :refer [deftest is]]))

(def single-fact-dp
  {:relations [{:from "01" :to "02" :role :segment}
               {:from "02" :to "03" :role :instance}]
   :concepts  [{:id "01" :type :document-plan}
               {:id "02" :type :segment}
               {:id "03" :type :data :value "title"}]})

(def modifier-dp
  {:relations [{:from "01" :to "02" :role :segment}
               {:from "02" :to "03" :role :instance}
               {:from "03" :to "04" :role :modifier}]
   :concepts  [{:id "01" :type :document-plan}
               {:id "02" :type :segment}
               {:id "03" :type :data :value "title"}
               {:id         "04"
                :type       :dictionary-item
                :value      "NN-good"
                :attributes {:name "good"}}]})

(def verb-dp
  {:relations [{:from "01" :to "02" :role :segment}
               {:from "02" :to "03" :role :instance}
               {:from "03" :to "05" :role :amr}
               {:from "05" :to "03" :role :arg0}
               {:from "05" :to "04" :role :arg1}]
   :concepts  [{:id "01" :type :document-plan}
               {:id "02" :type :segment}
               {:id "03" :type :data :value "title"}
               {:id "04" :type :data :value "author"}
               {:id "05" :type :amr :value "authorship"}]})


(deftest plan-realization
  (is (= ["Phrase. S ::= NP;"
          "Title. NP ::= \"{{TITLE}}\";"]
         (sut/dp->grammar single-fact-dp)))
  (is (= ["Phrase. S ::= AP;"
          "Compl-a. AP ::= A NP;"
          "Title. NP ::= \"{{TITLE}}\";"
          "Good. A ::= \"good\";"]
         (sut/dp->grammar modifier-dp)))
  (is (= ["Phrase. S ::= NP VP;"
          "Compl-v. VP ::= V2 NP;"
          "Authorship. V2 ::= \"authorship\";"
          "Title. NP ::= \"{{TITLE}}\";"
          "Author. NP ::= \"{{AUTHOR}}\";"]
         (sut/dp->grammar verb-dp))))
