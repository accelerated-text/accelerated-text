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
  (let [grammar (cf/vn->cf author-amr)]
    (is (= grammar ["Pred. S ::= VP;"
                    "Compl. VP ::= NP0 \"is\" \"the author of\" NP1;"
                    "Compl. VP ::= NP1 \"is\" V2 \"by\" NP0;"
                    "Action. V2 ::= \"written\";"
                    "Actor. NP0 ::= \"{{AGENT}}\";"
                    "Actor. NP1 ::= \"{{CO-AGENT}}\";"]))))
