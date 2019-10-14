(ns api.end-to-end-test
  (:require [api.test-utils :refer [with-dev-aws-credentials q load-test-data]]
            [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures join-fixtures]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]))

(defn prepare-environment [f]
  (ops/write! (ops/db-access :blockly) "1" (load-test-data "blockly/title_only") true)
  (ops/write! (ops/db-access :blockly) "2" (load-test-data "blockly/authorship") true)
  (f)
  (dp/delete-document-plan "1")
  (dp/delete-document-plan "2"))

(use-fixtures :each (join-fixtures [with-dev-aws-credentials prepare-environment]))

(defn wait-for-results [result-id]
  (when (some? result-id)
    (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
      (Thread/sleep 100))))

(deftest ^:integration single-element-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "1"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (not (string/blank? (do
                              (wait-for-results result-id)
                              (->> [:body :variants 0 :children 0 :children 0 :children]
                                   (get-in (q (str "/nlg/" result-id) :get nil))
                                   (map :text)
                                   (string/join " ")
                                   (string/trim))))))))

(deftest ^:integration authorship-document-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId   "2"
                         :readerFlagValues {}
                         :dataId           "example-user/books.csv"})]
    (is (= 200 status))
    (is (some? result-id))
    (is (not (string/blank? (do
                              (wait-for-results result-id)
                              (->> [:body :variants 0 :children 0 :children 0 :children]
                                   (get-in (q (str "/nlg/" result-id) :get nil))
                                   (map :text)
                                   (string/join " ")
                                   (string/trim))))))))
