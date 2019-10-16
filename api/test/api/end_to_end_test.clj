(ns api.end-to-end-test
  (:require [api.test-utils :refer [q load-test-data]]
            [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]))

(defn prepare-environment [f]
  (ops/write! (ops/db-access :blockly) "1" {:uid          "01"
                                            :name         "title-only"
                                            :documentPlan (load-test-data "document_plans/title-only")} true)
  (ops/write! (ops/db-access :blockly) "2" {:uid          "02"
                                            :name         "authorship"
                                            :documentPlan (load-test-data "document_plans/authorship")} true)
  (ops/write! (ops/db-access :blockly) "3" {:uid          "03"
                                            :name         "adjective-phrase"
                                            :documentPlan (load-test-data "document_plans/adjective-phrase")} true)
  (ops/write! (ops/db-access :dictionary-combined) "good" {:name         "good"
                                                           :partOfSpeech :NN
                                                           :phrases      [{:id    "good/1"
                                                                           :text  "good"
                                                                           :flags {:default :YES}}]})
  (f)
  (dp/delete-document-plan "1")
  (dp/delete-document-plan "2")
  (dp/delete-document-plan "3"))

(use-fixtures :each prepare-environment)

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
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "1"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (not (string/blank? (get-first-variant result-id))))))

(deftest ^:integration authorship-document-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "2"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (not (string/blank? (get-first-variant result-id))))))

(deftest ^:integration adjective-phrase-document-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "3"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (contains? #{"Building Search Applications ." "Good Building Search Applications ."}
                   (get-first-variant result-id)))))
