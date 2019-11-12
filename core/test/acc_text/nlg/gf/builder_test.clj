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

(def amr-with-modifier-dp
  #::sg{:relations [#::sg{:from "01" :to "02" :role :segment}
                    #::sg{:from "02" :to "03" :role :instance}
                    #::sg{:from "05" :to "03" :role :arg0}
                    #::sg{:from "05" :to "04" :role :arg1}
                    #::sg{:from "03" :to "07" :role :modifier}
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
                          :attributes #::sg{:name "author"}}
                    #::sg{:id         "07"
                          :type       :dictionary-item
                          :value      "NN-good"
                          :attributes #::sg{:name "good"}}]})

(def author-amr-with-adj-default
  #::sg{:relations [#::sg{:from "01" :to "02" :role :segment}
                    #::sg{:from "02" :to "03" :role :instance}
                    #::sg{:from "03" :to "04" :role :function}
                    #::sg{:from "03" :to "05" :role :ARG0 :attributes #::sg{:name "agent"}}
                    #::sg{:from "03" :to "07" :role :ARG1 :attributes #::sg{:name "co-agent"}}
                    #::sg{:from "05" :to "06" :role :modifier}]
        :concepts [#::sg{:id "01" :type :document-plan}
                   #::sg{:id "02" :type :segment}
                   #::sg{:id "03" :type :amr :value "author"
                         :attributes #::sg{:syntax [[{:pos :NP :value "Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :LEX :value "the author of"}
                                                     {:pos :NP  :value "co-Agent"}]
                                                    [{:pos :NP :value "co-Agent"}
                                                     {:pos :LEX :value "is"}
                                                     {:pos :VERB}
                                                     {:pos :PREP :value "by"}
                                                     {:pos :NP :value "Agent"}]]}}
                   #::sg{:id "04"
                         :type :dictionary-item,
                         :value "written",
                         :attributes #::sg{:name "written",
                                           :reader-profile :default},
                         :members []}
                   #::sg{:id "05" :type :data :value "authors"}
                   #::sg{:id "06" :type :dictionary-item :value "good"
                         :attributes #::sg{:name "good"
                                           :reader-profile :default}
                         :members []}
                   #::sg{:id "07" :type :data :value "title"}]})


(deftest plan-realization
  (is (= ["Phrase. S ::= NP;"
          "Title. NP ::= \"{{TITLE}}\";"]
         (builder/build-grammar single-fact-dp)))
  (is (= ["Phrase. S ::= NP;"
          "Title. NP ::= A \"{{TITLE}}\";"
          "Good. A ::= \"good\";"]
         (builder/build-grammar modifier-dp)))
  (is (= ["Phrase. S ::= NP VP;"
          "ComplV2. VP ::= V2 NP;"
          "Authoramr. V2 ::= \"wrote\";"
          "Title. NP ::= \"{{TITLE}}\";"
          "Author. NP ::= \"{{AUTHOR}}\";"]
         (builder/build-grammar verb-dp)))
  (is (= ["Phrase. S ::= NP VP;"
          "ComplV2. VP ::= V2 NP;"
          "Authoramr. V2 ::= \"wrote\";"
          "Title. NP ::= A \"{{TITLE}}\";"
          "Author. NP ::= \"{{AUTHOR}}\";"
          "Good. A ::= \"good\";"]
         (builder/build-grammar amr-with-modifier-dp))))
