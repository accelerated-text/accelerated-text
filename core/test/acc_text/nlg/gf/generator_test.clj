(ns acc-text.nlg.gf.generator-test
  (:require [acc-text.nlg.gf.generator :as sut]
            [acc-text.nlg.test-utils :as utils]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is]]))

(stest/instrument `acc-text.nlg.gf.grammar/build)

(deftest ^:integration quote-cases
  (is (= ["He said: \"GO!\""]
         (sut/generate
          {::sg/graph (utils/load-test-semantic-graph "quote-quote")}))))
