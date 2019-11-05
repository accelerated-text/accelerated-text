(ns acc-text.nlg.gf.cf-format-test
  (:require [acc-text.nlg.gf.cf-format :as sut]
            [clojure.test :refer [deftest is]]))

(deftest gf-item-construction
  (is (= "Pred. S ::= NP VP;" (sut/gf-syntax-item "Pred" "S" "NP VP"))))

