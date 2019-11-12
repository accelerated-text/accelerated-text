(ns acc-text.nlg.generate-test
  (:require [clojure.test :refer [deftest is]]
            [acc-text.nlg.generate :as generate]
            [acc-text.nlg.spec.semantic-graph :as sg]
            [acc-text.nlg.test-utils :as utils]))

(deftest semantic-graph-instance-parsing
  (let [sgi (utils/load-test-semantic-graph-instance "author-amr-with-adj-default")]
    (generate/build-grammar sgi)))

