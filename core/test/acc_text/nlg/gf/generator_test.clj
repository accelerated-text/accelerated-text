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
               grammar (grammar/build :module :instance semantic-graph {})]
           (generate "module" (grammar/->abstract grammar) (list [1 (grammar/->concrete grammar)]))))))
