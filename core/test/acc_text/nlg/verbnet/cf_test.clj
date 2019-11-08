(ns acc-text.nlg.verbnet.cf-test
  (:require [acc-text.nlg.verbnet.cf :as cf]
            [clojure.test :refer [deftest is testing]]))

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
   (list
    {:type "Agent",
     :selection-restrictions
     ({:restrictors ({:value "+", :type "int_control"})})}
    {:type "Patient",
     :selection-restrictions
     ({:restrictors ({:value "+", :type "concrete"})})}
    {:type "Instrument",
     :selection-restrictions
     ({:restrictors ({:value "+", :type "concrete"})})}
    {:type "Source"}
    {:type "Result"}),
   :frames
   (list
    {:examples ("Carol cut the envelope into pieces with a knife."),
     :syntax
     (list
      {:pos :NP, :value "Agent"}
      {:pos :VERB}
      {:pos :NP, :value "Patient"}
      {:pos :PREP, :value "to into"}
      {:pos :NP,
       :value "Result",
       :restrictors {:restrictors ({:value "+", :type "state"})}}
      {:pos :PREP, :value "with"}
      {:pos :NP, :value "Instrument"})})})

(deftest author-amr-to-cf
  (let [grammar (cf/vn->cf author-amr)]
    (is (= grammar ["Pred. S ::= VP;"
                    "Compl. VP ::= NP0 \"is\" \"the author of\" NP1;"
                    "Compl. VP ::= NP1 \"is\" V2 \"by\" NP0;"
                    "Action. V2 ::= \"written\";"
                    "Actor. NP0 ::= \"{{AGENT}}\";"
                    "Actor. NP1 ::= \"{{CO-AGENT}}\";"]))))
