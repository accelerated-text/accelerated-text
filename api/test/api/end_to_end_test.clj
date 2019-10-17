(ns api.end-to-end-test
  (:require [api.test-utils :refer [q load-test-document-plan]]
            [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]))

(defn valid-sentence?
  "Test validity of the sentence.
  String is considered a valid sentence if it:
  1. ends with one of the following punctuation symbols - .!?
  2. has at least two alphanumeric characters."
  [txt] (re-matches #"(\S{2,} )+[.?!]" txt))

(defn prepare-environment [f]
  (doseq [[id filename] [["1" "title-only"]
                         ["2" "authorship"]
                         ["3" "adjective-phrase"]
                         ["4" "author-amr"]
                         ["5" "author-amr-with-adj"]]]
    (ops/write! (ops/db-access :blockly) id {:uid          id
                                             :name         filename
                                             :documentPlan (load-test-document-plan filename)} true))
  (ops/write! (ops/db-access :dictionary-combined) "good" {:name         "good"
                                                           :partOfSpeech :NN
                                                           :phrases      [{:id    "good/1"
                                                                           :text  "good"
                                                                           :flags {:default :YES}}]})
  (f)
  (doseq [id ["1" "2" "3" "4" "5"]]
    (dp/delete-document-plan id)))

(use-fixtures :each prepare-environment)

(defn wait-for-results [result-id]
  (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
    (Thread/sleep 100)))

(defn get-variants [result-id]
  (when (some? result-id)
    (wait-for-results result-id)
    (let [response (q (str "/nlg/" result-id) :get nil)]
      (for [variant (get-in response [:body :variants])]
        (->> (get-in variant [:children 0 :children 0 :children])
             (map :text)
             (string/join " ")
             (string/trim))))))

(deftest ^:integration single-element-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "1"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (first (get-variants result-id))))))

(deftest ^:integration authorship-document-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "2"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (valid-sentence? (first (get-variants result-id))))))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "3"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Building Search Applications ." "Good Building Search Applications ."}
                   (first (get-variants result-id))))))

(deftest ^:integration author-amr-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "4"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (some? (first (get-variants result-id))))))

(deftest ^:integration author-amr-with-adjective-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "5"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (some #(string/includes? % "good") (get-variants result-id)))))
