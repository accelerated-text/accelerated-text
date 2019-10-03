(ns acc-text.nlg.combinator-test
  (:import [opennlp.ccg.lexicon Word])
  (:require [acc-text.nlg.combinator :as sut]
            [clojure.test :refer [deftest is]]))

(deftest implicit-filters
  (let [run (Word/createWord "run")]
    (is (sut/filter-implicit (Word/createFullWord run "run" "P" "_" "_")))
    (is (not (sut/filter-implicit (Word/createFullWord run "run" "VB" "_" "_"))))))
