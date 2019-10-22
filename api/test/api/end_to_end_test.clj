(ns api.end-to-end-test
  (:require [api.graphql.ddb-fixtures :as ddb-fixtures]
            [api.test-utils :refer [q load-test-document-plan]]
            [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]
            [data.entities.data-files :as data-files]))

(defn valid-sentence?
  "Test validity of the sentence.
  String is considered a valid sentence if it:
  1. ends with one of the following punctuation symbols - .!?
  2. has at least two alphanumeric characters."
  [txt] (re-matches #"(\S{2,} )+[.?!]" txt))

(defn prepare-environment [f]
  (ops/write! (ops/db-access :blockly) "1" {:uid          "01"
                                            :name         "title-only"
                                            :documentPlan (load-test-document-plan "title-only")} true)
  (ops/write! (ops/db-access :blockly) "2" {:uid          "02"
                                            :name         "authorship"
                                            :documentPlan (load-test-document-plan "authorship")} true)
  (ops/write! (ops/db-access :blockly) "3" {:uid          "03"
                                            :name         "adjective-phrase"
                                            :documentPlan (load-test-document-plan "adjective-phrase")} true)
  (ops/write! (ops/db-access :blockly) "4" {:uid          "04"
                                            :name         "author-amr"
                                            :documentPlan (load-test-document-plan "author-amr")} true)
  (ops/write! (ops/db-access :dictionary-combined) "good" {:name         "good"
                                                           :partOfSpeech :NN
                                                           :phrases      [{:id    "good/1"
                                                                           :text  "good"
                                                                           :flags {:default :YES}}]})
  (f)
  (dp/delete-document-plan "1")
  (dp/delete-document-plan "2")
  (dp/delete-document-plan "3")
  (dp/delete-document-plan "4"))

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
                        :content  (slurp "resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "1"
                         :readerFlagValues {}
                         :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration authorship-document-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "2"
                         :readerFlagValues {}
                         :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (get-first-variant result-id)))))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "3"
                         :readerFlagValues {}
                         :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Building Search Applications ." "Good Building Search Applications ."}
                   (get-first-variant result-id)))))

(deftest ^:integration author-amr-plan-generation
  (let [data-file-id (data-files/store!
                       {:filename "example-user/books.csv"
                        :content  (slurp "resources/accelerated-text-data-files/example-user/books.csv")})
        {{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "4"
                         :readerFlagValues {}
                         :dataId           data-file-id})]
    (is (= 200 status))
    (is (some? result-id))
    (is (some? (get-first-variant result-id)))))
