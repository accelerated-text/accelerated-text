(ns lt.tokenmill.nlg.api.generate-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.api.utils :refer :all]
            [lt.tokenmill.nlg.api.generate :refer :all]))


;; DISABLED until proper DynamoDB mocking will be made
;; (deftest basic-generation
;;   (let [documentPlanId "6c91d58d-6815-4269-93f7-33673be9400c"
;;         dataId "example-user/data-example.csv"
;;         result-fn (fn [body]
;;                     (println (format "Generation result: %s", body))
;;                     (is (= body {})))
;;         result (generation-process documentPlanId dataId result-fn)]))
