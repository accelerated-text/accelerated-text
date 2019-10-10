(ns api.end-to-end-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [api.test-utils :refer [q]]
            [data.db.dynamo-ops :as ops]
            [data.entities.document-plan :as dp]
            [clojure.string :as string]))

(defn prepare-environment [f]
  (System/setProperty  "aws.region" "eu-central-1")
  (ops/write! (ops/db-access :blockly) "1"
              {:uid        "01"
               :name       "authorship"
               :blocklyXml (slurp "test/resources/blockly/authorship.xml")
               :documentPlan
               {:segments
                [{:children
                  [{:type      "AMR"
                    :srcId     "WlSg"
                    :conceptId "author"
                    :dictionaryItem
                    {:name  "author" :type   "Dictionary-item"
                     :srcId "jirq"   :itemId "VB-author"}
                    :roles
                    [{:name     "agent"
                      :children [{:name "authors" :type "Cell" :srcId "uakxT"}]}
                     {:name     "co-agent"
                      :children [{:name "title" :type "Cell" :srcId "X_Cw"}]}
                     {:name "theme" :children [nil]}]}]
                  :type "Segment" :srcId "0Ci"}]
                :type "Document-plan" :srcId "eoPNHZ1"}}
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
  (testing "Single title element plan"
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
                      (string/join " ")))))))))

(deftest authorship-document-plan-generation
  (testing "Authorship plan"
    (let [{{result-id :resultId} :body status :status}
          (q "/nlg" :post {:documentPlanId "1"
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
                      (string/join " ")))))))))

