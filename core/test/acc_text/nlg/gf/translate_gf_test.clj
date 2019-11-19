(ns acc-text.nlg.gf.translate-gf-test
  (:require [acc-text.nlg.gf.translate-gf :as sut]
            [clojure.test :refer [is deftest]]))

(deftest build-empty-abstract-grammar
  (is (= "abstract Test {\n\n}\n" (sut/abstract->gf {:acc-text.nlg.gf.grammar/module-name "Test"}))))
