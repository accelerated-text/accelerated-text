(ns lt.tokenmill.nlg.api.generate-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.api.utils :refer :all]
            [lt.tokenmill.nlg.api.generate :refer :all]))

(deftest ^:integration basic-generation
  (let [documentPlanId "8fa07eda-68d1-480f-a8e5-d39385977ca3"
        dataId "example-user/data-example.csv"
        result-fn (fn [body]
                    (println (format "Generation result: %s", body))
                    (let [results (:results body)
                          first-result (first results)]
                      (is (= 1 (count results)))))
        result (generation-process documentPlanId dataId result-fn false)]))

(deftest basic-generation-ccg
  (let [documentPlanId "8fa07eda-68d1-480f-a8e5-d39385977ca3"
        dataId "example-user/ccg-example.csv"
        result-fn (fn [body]
                    (println (format "Generation result: %s", body))
                    (let [results (:results body)
                          first-result (first results)]
                      (is (= 1 (count results)))))
        result (generation-process documentPlanId dataId result-fn true)]))
