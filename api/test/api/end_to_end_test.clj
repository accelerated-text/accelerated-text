(ns api.end-to-end-test
  (:require [api.db-fixtures :as db]
            [api.test-utils :refer [generate-text generate-text-bulk]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each db/clean-db)

(deftest text-generation-with-default-plan
  (is (= [] (generate-text {:document-plan-name "Untitled plan"})))
  (is (= {"test" []} (generate-text-bulk {:document-plan-name "Untitled plan"
                                          :data-rows          {"test" {}}}))))
