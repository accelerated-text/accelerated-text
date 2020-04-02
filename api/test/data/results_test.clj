(ns data.results-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.results :as results]
            [data.spec.result :as result]
            [data.test-utils :refer [read-test-result]]
            [api.db-fixtures :as fixtures]))

(use-fixtures :each fixtures/clean-db)

(deftest results-io
  (let [{result-id ::result/id :as sample-result} (read-test-result "sample-result")]
    (results/write sample-result)
    (is (= sample-result (-> (results/fetch result-id) (dissoc ::result/timestamp))))
    (is (contains? (results/fetch result-id) ::result/timestamp))))
