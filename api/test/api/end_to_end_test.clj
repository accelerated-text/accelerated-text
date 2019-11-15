(ns api.end-to-end-test
  (:require [api.db-fixtures :as fixtures]
            [api.test-utils :refer [q load-test-document-plan rebuild-sentence]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dictionary]))

(defn valid-sentence?
  "Test validity of the sentence.
  String is considered a valid sentence if it:
  1. ends with one of the following punctuation symbols - .!?
  2. has at least two alphanumeric characters."
  [txt] (re-matches #".*\w{2,}.*[.?!]" txt))

(defn prepare-environment [f]
  (dictionary/create-dictionary-item {:key "cut"
                                      :name "cut"
                                      :phrases ["cut"]
                                      :partOfSpeech :VB})
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
  (dp/add-document-plan {:uid          "05"
                         :name         "single-quote"
                         :documentPlan (load-test-document-plan "single-quote")}
                        "5")
  (dp/add-document-plan {:uid          "06"
                         :name         "single-modifier"
                         :documentPlan (load-test-document-plan "single-modifier")}
                        "6")
  (dp/add-document-plan {:uid          "07"
                         :name         "cut-amr"
                         :documentPlan (load-test-document-plan "cut-amr")}
                        "7")
  (f))

(use-fixtures :each fixtures/clean-db prepare-environment)

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
    (Thread/sleep 100)))

(defn get-first-variant [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil)]
      (rebuild-sentence
       (get-in response [:body :variants 0 :children 0 :children 0 :children])))))

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
    (is (contains? #{"Building Search Applications." "Good Building Search Applications."}
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
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration single-quote-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "5"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "This is a very good book: Building Search Applications." (get-first-variant result-id)))))


(deftest ^:integration single-modifier-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "6"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "Good." (get-first-variant result-id)))))

(deftest ^:integration complex-amr-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/carol.csv"
                        :content  (slurp "test/resources/accelerated-text-data-files/example-user/carol.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg/" :post {:documentPlanId   "7"
                          :readerFlagValues {}
                          :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (= "Carol cut envelope to into pieces with knife." (get-first-variant result-id)))))
