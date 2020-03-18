(ns acc-text.nlg.core-test
  (:require [acc-text.nlg.core :as core]
            [acc-text.nlg.test-utils :as test-utils]
            [clojure.test :refer [deftest are]]))

(deftest ^:integration multi-language-generation
  (let [semantic-graph (test-utils/load-test-semantic-graph "language-test")
        context (test-utils/load-test-context "language-test")]
    (are [lang result] (= result (map :text (core/generate-text semantic-graph context lang)))
                       "Eng" ["There is a text."]
                       "Est" ["On olemas text."]
                       "Ger" ["Es gibt einen text."]
                       "Lav" ["Ir text."]
                       "Rus" ["Существует text."])))
