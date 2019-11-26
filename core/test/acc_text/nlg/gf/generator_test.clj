(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :refer [generate]]
            [acc-text.nlg.gf.grammar.cf :as cf-grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `acc-text.nlg.gf.grammar/build)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\""] (-> "quote-quote" (utils/load-test-semantic-graph) (cf-grammar/build {}) (generate)))))
