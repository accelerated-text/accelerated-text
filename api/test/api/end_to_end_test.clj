(ns api.end-to-end-test
  (:require [api.test-utils :refer [q load-test-data]]
            [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]))

(defn prepare-environment [f]
  (System/setProperty  "aws.region" "eu-central-1")
  (ops/write! (ops/db-access :blockly) "1"
              (load-test-data "blockly/authorship")
              true)
  (ops/write! (ops/db-access :blockly) "2"
              {:uid        "02"
               :name       "title-only"
               :blocklyXml (slurp "test/resources/blockly/title-only.xml")
               :documentPlan
               {:segments [{:children [{:name "title" :type "Cell" :srcId "isiyTw"}]
                            :type     "Segment" :srcId "%!Y"}]
                :type     "Document-plan"
                :srcId    "G=Rh"}}
              true)
  (f)
  (dp/delete-document-plan "1")
  (dp/delete-document-plan "2"))

(use-fixtures :each prepare-environment)

(deftest single-element-plan-generation
  (let [{{result-id :resultId} :body status :status}
        (q "/nlg" :post {:documentPlanId "2"
                         :readerFlagValues {}
                         :dataId "example-user/books.csv"})]

    (Thread/sleep 1000)
    (is (= 200 status))
    (is (some? result-id))
    (let [generation-results (q (str "/nlg/" result-id) :get nil)]
      (is (not(string/blank?
               (->> (get-in generation-results [:body :variants])
                    (first) (:children)
                    (first) (:children)
                    (first) (:children)
                    (map :text)
                    (string/join " "))))))))

(defn wait-for-results [result-id]
  (when (some? result-id)
    (while (false? (get-in (q (str "/nlg/" result-id) :get nil) [:body :ready]))
      (Thread/sleep 100))))

(deftest full-document-plan-generation
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
