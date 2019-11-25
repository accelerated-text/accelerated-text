(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :refer [generate]]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\""]
         (let [semantic-graph (utils/load-test-semantic-graph "quote-quote")
               grammar (grammar/build :grammar :grammar1 semantic-graph {})]
           (generate "grammar" (grammar/->abstract grammar) (list [1 (grammar/->concrete grammar)]))))))
