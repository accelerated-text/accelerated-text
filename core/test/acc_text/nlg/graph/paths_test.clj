(ns acc-text.nlg.graph.paths-test
  (:require [acc-text.nlg.test-utils :refer [load-test-semantic-graph load-test-context]]
            [acc-text.nlg.core :as core]
            [clojure.test :refer [deftest is]]))

(deftest ^:integration path-completion
  (let [semantic-graph (load-test-semantic-graph "path-test")
        context (load-test-context "path-test")]
    (is (= #{"It is made of steel."
             "It is made."
             "It makes itself."
             "Of steel."
             "Of."
             "Refrigerator is made of steel."
             "Refrigerator was made of steel."
             "Refrigerator will be made of steel."
             "There is refrigerator."
             "There is steel."}
           (set (map :text (core/generate-text semantic-graph context "Eng")))))))
