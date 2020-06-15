(ns api.end-to-end-test
  (:require [api.db-fixtures :as db]
            [api.test-utils :refer [generate-text generate-text-bulk load-document-plan load-dictionary]]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each db/clean-db)

(deftest ^:integration text-generation-with-default-plan
  (is (= [] (generate-text {:document-plan-name "Untitled plan"})))
  (is (= {"test" []} (generate-text-bulk {:document-plan-name "Untitled plan"
                                          :data-rows          {"test" {}}}))))

(deftest ^:integration text-generation-with-simple-pan
  (is (= ["Building Search Applications is authored by Manu Konchady."]
         (generate-text {:document-plan-name "Authorship"
                         :data-file-name     "books.csv"})))
  (is (= {"0" ["Building Search Applications is authored by Manu Konchady."]
          "1" ["The Business Blockchain is authored by William Mougayar."]}
         (generate-text-bulk {:document-plan-name "Authorship"
                              :data-rows          {"0" {"authors" "Manu Konchady"
                                                        "title"   "Building Search Applications"}
                                                   "1" {"authors" "William Mougayar"
                                                        "title"   "The Business Blockchain"}}}))))

(deftest ^:integration text-generation-with-complex-plan
  (load-dictionary "authorship")
  (load-document-plan "AuthorshipRGL")
  (load-document-plan "AuthorshipAMR")
  (is (= #{"Building Search Applications is written by Manu Konchady."
           "Building Search Applications wird durch Manu Konchady geschrieben."}
         (set (generate-text {:document-plan-name "AuthorshipMultiLang"
                              :data-file-name     "books.csv"
                              :reader-flags       {"English" true
                                                   "German"  true}})))))
