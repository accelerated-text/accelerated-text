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

(def complex-amr-dp )

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

(def author-amr
  {:id "author"
   :members [{:name "written"}]
   :thematic-roles
   (list {:type "Agent"}
         {:type "co-Agent"})
   :frames
   (list
    {:examples (list "X is the author of Y")
     :syntax
     (list
      {:pos :NP :value "Agent"}
      {:pos :LEX :value "is"}
      {:pos :LEX :value "the author of"}
      {:pos :NP :value "co-Agent"})}
    {:examples (list "Y is written by X")
     :syntax
     (list
      {:pos :NP :value "co-Agent"}
      {:pos :LEX :value "is"}
      {:pos :VERB}
      {:pos :PREP :value "by"}
      {:pos :NP :value "Agent"})})})

(def cut-amr
  {:id "cut-21.1",
   :members [{:name "cut"}]
   :thematic-roles
   [{:type "Agent"}
    {:type "Patient"}
    {:type "Instrument"}
    {:type "Source"}
    {:type "Result"}]
   :frames
   [{:examples ["Carol cut the envelope into pieces with a knife."]
     :syntax
     [{:pos :NP :value "Agent"}
      {:pos :VERB}
      {:pos :NP :value "Patient"}
      {:pos :PREP :value "to into"}
      {:pos :NP :value "Result"}
      {:pos :PREP :value "with"}
      {:pos :NP :value "Instrument"}]}]})

(deftest author-amr-to-cf
  (let [grammar (builder/vn->cf author-amr)]
    (is (= grammar ["Pred. S ::= VP;"
                    "Compl. VP ::= NP0 \"is\" \"the author of\" NP1;"
                    "Compl. VP ::= NP1 \"is\" V2 \"by\" NP0;"
                    "Action. V2 ::= \"written\";"
                    "Agent. NP0 ::= \"{{AGENT}}\";"
                    "Co-agent. NP1 ::= \"{{CO-AGENT}}\";"]))))

(deftest cut-amr-to-cf
  (let [grammar (builder/vn->cf cut-amr)]
    (is (= grammar ["Pred. S ::= VP;"
                    "Compl. VP ::= NP0 V2 NP1 \"to into\" NP4 \"with\" NP2;"
                    "Action. V2 ::= \"cut\";"
                    "Agent. NP0 ::= \"{{AGENT}}\";"
                    "Patient. NP1 ::= \"{{PATIENT}}\";"
                    "Instrument. NP2 ::= \"{{INSTRUMENT}}\";"
                    "Source. NP3 ::= \"{{SOURCE}}\";"
                    "Result. NP4 ::= \"{{RESULT}}\";"]))))

