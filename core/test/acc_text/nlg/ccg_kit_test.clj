(ns acc-text.nlg.ccg-kit-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.tools.logging :as log]
            [acc-text.nlg.grammar :as g]
            [acc-text.nlg.predefined :as predefined]))

(deftest test-generate
  (let [grammar (predefined/custom-build-grammar)]
    (testing "Generate basic string"
      (let [results (g/generate grammar "Nike1" "comfort" "support" "provides")]
        (is (not (nil? results)))
        (log/info "Generated results:")
        (doseq [r results] (log/info r))
        (is (= 30 (count results)))))
    (testing "Generate failing string"
      (let [results (g/generate grammar "Nike1" "comfort" "support" "good" "provides")]
        (is (not (nil? results)))
        (log/info "Generated results:")
        (doseq [r results] (log/info r))
        (is (= 30 (count results)))))))
