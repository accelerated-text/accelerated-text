(ns api.end-to-end-test
  (:require [api.ddb-fixtures :as ddb-fixtures]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]))

(defn valid-sentence?
  "Test validity of the sentence.
  String is considered a valid sentence if it:
  1. ends with one of the following punctuation symbols - .!?
  2. has at least two alphanumeric characters."
  [txt] (re-matches #"(\S{2,} )+[.?!]" txt))

(defn prepare-environment [f]
  (dp/add-document-plan {:uid          "01"
                         :name         "title-only"
                         :documentPlan (load-test-document-plan "title-only")}
                        "1")
  (dp/add-document-plan {:uid          "02"
                         :name         "authorship"
                         :documentPlan (load-test-document-plan "authorship")}
                        "2")
  (dp/add-document-plan {:uid          "03"
                         :name         "adjective-phrase"
                         :documentPlan (load-test-document-plan "adjective-phrase")}
                        "3")
  (dp/add-document-plan {:uid          "04"
                         :name         "author-amr"
                         :documentPlan (load-test-document-plan "author-amr")}
                        "4")
  (f))

(use-fixtures :each ddb-fixtures/wipe-ddb-tables prepare-environment)

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
    (Thread/sleep 100)))

(defn get-first-variant [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil)]
      (->> (get-in response [:body :variants 0 :children 0 :children 0 :children])
           (map :text)
           (string/join " ")
           (string/trim)))))

(deftest ^:integration single-element-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "1"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration authorship-document-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "2"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "3"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Building Search Applications ." "Good Building Search Applications ."}
                   (get-first-variant result-id)))))

(deftest ^:integration author-amr-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "4"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (some? (get-first-variant result-id)))))
