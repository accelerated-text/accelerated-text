(ns acc-text.nlg.graph.paths-test
  (:require [acc-text.nlg.test-utils :refer [load-test-semantic-graph load-test-context]]
            [acc-text.nlg.core :as core]
            [clojure.test :refer [deftest is]]))

(deftest ^:integration path-completion
  (let [semantic-graph (load-test-semantic-graph "path-test")
        context (load-test-context "path-test")]
    (is (= #{"To be made of steel."
             "To be made."
             "To make itself."
             "Of steel."
             "Of."
             "Refrigerator is made of steel."
             "Refrigerator was made of steel."
             "Refrigerator will be made of steel."
             "Refrigerator."
             "Steel."}
           (set (map :text (core/generate-text semantic-graph context "Eng")))))))
