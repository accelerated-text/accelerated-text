(ns acc-text.nlg.graph.paths-test
  (:require [acc-text.nlg.test-utils :refer [load-test-semantic-graph load-test-context]]
            [acc-text.nlg.core :as core]
            [clojure.test :refer [deftest is testing]]))

(deftest ^:integration path-completion
  (let [semantic-graph (load-test-semantic-graph "path-test")
        context (load-test-context "path-test")]
    (is (= #{"Be made of steel!"
             "Be made!"
             "Make yourself!"
             "Of steel."
             "Of."
             "Refrigerator is made of steel."
             "Refrigerator was made of steel."
             "Refrigerator will be made of steel."
             "There is a refrigerator."
             "There is a steel."
             "There is steel."}
           (set (map :text (core/generate-text semantic-graph context "Eng")))))))