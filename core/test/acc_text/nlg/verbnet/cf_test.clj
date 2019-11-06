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

(deftest author-amr-to-cf
  (let [grammars (set (cf/vn->cf author-amr))]
    (is (contains? grammars ["Pred. S ::= NP0 VP;"
                             "Compl. VP ::= \"is\" \"the author of\" NP1;"
                             "Action. VB ::= \"written\";"
                             "Actor. NP0 ::= \"{{AGENT}}\";"
                             "Actor. NP1 ::= \"{{CO-AGENT}}\";"]))
    (is (contains? grammars ["Pred. S ::= NP1 VP;"
                             "Compl. VP ::= \"is\" VB \"by\" NP0;"
                             "Action. VB ::= \"written\";"
                             "Actor. NP0 ::= \"{{AGENT}}\";"
                             "Actor. NP1 ::= \"{{CO-AGENT}}\";"]))))
