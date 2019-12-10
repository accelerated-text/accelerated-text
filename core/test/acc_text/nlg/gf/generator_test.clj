(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :refer [generate]]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.test-utils :as utils]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `grammar/build `generate)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\""] (let [semantic-graph (utils/load-test-semantic-graph "quote")
                                    grammar (grammar/build :grammar :1 semantic-graph {})]
                                (generate grammar)))))
