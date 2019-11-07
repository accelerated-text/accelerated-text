(ns acc-text.nlg.gf.builder-test
  (:require [acc-text.nlg.gf.builder :as builder]
            [acc-text.nlg.spec.semantic-graph :as sg]
            [clojure.test :refer [deftest is]]))

(def single-fact-dp
  #::sg{:relations [#::sg{:from "01" :to "02" :role :segment}
                    #::sg{:from "02" :to "03" :role :instance}]
        :concepts  [#::sg{:id "01" :type :document-plan}
                    #::sg{:id "02" :type :segment}
                    #::sg{:id "03" :type :data :value "title"}]})

(def modifier-dp
  #::sg{:relations [#::sg{:from "01" :to "02" :role :segment}
                    #::sg{:from "02" :to "03" :role :instance}
                    #::sg{:from "03" :to "04" :role :modifier}]
        :concepts  [#::sg{:id "01" :type :document-plan}
                    #::sg{:id "02" :type :segment}
                    #::sg{:id "03" :type :data :value "title"}
                    #::sg{:id         "04"
                          :type       :dictionary-item
                          :value      "NN-good"
                          :attributes #::sg{:name "good"}}]})

(def verb-dp
  #::sg{:relations [#::sg{:from "01" :to "02" :role :segment}
                    #::sg{:from "02" :to "03" :role :instance}
                    #::sg{:from "03" :to "05" :role :amr}
                    #::sg{:from "05" :to "03" :role :arg0}
                    #::sg{:from "05" :to "04" :role :arg1}
                    #::sg{:from "05" :to "06" :role :function}]
        :concepts  [#::sg{:id "01" :type :document-plan}
                    #::sg{:id "02" :type :segment}
                    #::sg{:id "03" :type :data :value "title"}
                    #::sg{:id "04" :type :data :value "author"}
                    #::sg{:id "05" :type :amr :value "authorship"}
                    #::sg{:id         "06"
                          :type       :dictionary-item
                          :value      "VB-author"
                          :members    ["wrote"]
                          :attributes #::sg{:name "author"}}]})

(deftest plan-realization
  (is (= ["Phrase. S ::= NP;"
          "Title. NP ::= \"{{TITLE}}\";"]
         (builder/build-grammar single-fact-dp)))
  (is (= ["Phrase. S ::= AP;"
          "ComplA. AP ::= A NP;"
          "Title. NP ::= \"{{TITLE}}\";"
          "Good. A ::= \"good\";"]
         (builder/build-grammar modifier-dp)))
  (is (= ["Phrase. S ::= NP VP;"
          "ComplV2. VP ::= V2 NP;"
          "Author. V2 ::= \"wrote\";"
          "Title. NP ::= \"{{TITLE}}\";"
          "Author. NP ::= \"{{AUTHOR}}\";"]
         (builder/build-grammar verb-dp))))
