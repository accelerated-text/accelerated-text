(ns nlg.api.generate-test
  (:require [clojure.test :refer [deftest is]]
            [nlg.api.generate :refer [generation-process]]))

(deftest ^:integration basic-generation
  (let [documentPlanId "8fa07eda-68d1-480f-a8e5-d39385977ca3"
        dataId "example-user/data-example.csv"
        result-fn (fn [body]
                    (println (format "Generation result: %s", body))
                    (let [results (:results body)]
                      (is (= 1 (count results)))))]
    (generation-process documentPlanId dataId result-fn false)))

(deftest ^:integration basic-generation-ccg
  (let [documentPlanId "8fa07eda-68d1-480f-a8e5-d39385977ca3"
        dataId "example-user/ccg-example.csv"
        result-fn (fn [body]
                    (println (format "Generation result: %s", body))
                    (let [results (:results body)]
                      (is (= 1 (count results)))))]
    (generation-process documentPlanId dataId result-fn true)))
