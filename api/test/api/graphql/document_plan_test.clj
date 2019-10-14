(ns api.graphql.document-plan-test
  (:require [api.test-utils :refer [with-dev-aws-credentials q]]
            [clojure.test :refer [deftest is use-fixtures join-fixtures]]
            [data.entities.document-plan :as dp]
            [data.db.dynamo-ops :as ops]))

(defn prepare-environment [f]
  (ops/write! (ops/db-access :blockly)
              "1"
              {:uid          "01"
               :name         "test"
               :blocklyXml   "<>"
               :documentPlan "{}"}
              true)
  (f)
  (dp/delete-document-plan "1"))

(use-fixtures :each (join-fixtures [with-dev-aws-credentials prepare-environment]))

(deftest ^:integration document-plans-test
  (let [query "{documentPlans(offset:%s limit:%s){items{id uid name blocklyXml documentPlan dataSampleId dataSampleRow createdAt updatedAt updateCount} offset limit totalCount}}"
        {{{{:keys [items offset limit totalCount]} :documentPlans} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query 0 20)})]
    (is (nil? errors))
    (is (seq items))
    (is (zero? offset))
    (is (= 20 limit))
    (is (pos-int? totalCount))))

(deftest ^:integration document-plan-test
  (let [query "{documentPlan(id:\"%s\"){id uid name blocklyXml documentPlan createdAt updatedAt updateCount}}"
        {{{{:keys [id uid name blocklyXml documentPlan createdAt updatedAt updateCount]} :documentPlan} :data errors :errors} :body}
        (q "/_graphql" :post {:query (format query "1")})]
    (is (nil? errors))
    (is (= "1" id))
    (is (= "01" uid))
    (is (= "test" name))
    (is (= "<>" blocklyXml))
    (is (= "\"{}\"" documentPlan))
    (is (pos-int? createdAt))
    (is (= createdAt updatedAt))
    (is (zero? updateCount))))

(deftest ^:integration create-document-plan-test
  (let [query "mutation createDocumentPlan($uid: ID! $name: String! $blocklyXml: String! $documentPlan: String! $dataSampleRow: Int){createDocumentPlan(uid: $uid name: $name blocklyXml: $blocklyXml documentPlan: $documentPlan dataSampleRow: $dataSampleRow){ id uid name blocklyXml documentPlan dataSampleRow createdAt updatedAt updateCount }}"
        {{{{:keys [id uid name blocklyXml documentPlan dataSampleRow createdAt updatedAt updateCount]} :createDocumentPlan} :data errors :errors} :body}
        (q "/_graphql" :post {:query     query
                              :variables {:uid          "02"
                                          :name         "test"
                                          :blocklyXml   "<>"
                                          :documentPlan "\"{}\""}})]
    (is (nil? errors))
    (is (string? id))
    (is (= "02" uid))
    (is (= "test" name))
    (is (= "<>" blocklyXml))
    (is (= "\"{}\"" documentPlan))
    (is (nil? dataSampleRow))
    (is (pos-int? createdAt))
    (is (= createdAt updatedAt))
    (is (zero? updateCount))))

(deftest ^:integration update-document-plan-test
  (let [query "mutation updateDocumentPlan($id: ID! $uid: ID $name: String $blocklyXml: String $documentPlan: String $dataSampleRow: Int){updateDocumentPlan(id: $id uid: $uid name: $name blocklyXml: $blocklyXml documentPlan: $documentPlan dataSampleRow: $dataSampleRow){ id uid name blocklyXml documentPlan dataSampleRow createdAt updatedAt updateCount}}"
        {{{{:keys [id uid name blocklyXml documentPlan dataSampleRow createdAt updatedAt updateCount]} :updateDocumentPlan} :data errors :errors} :body}
        (q "/_graphql" :post {:query     query
                              :variables {:id            "1"
                                          :uid           "01"
                                          :name          "test-updated"
                                          :blocklyXml    "<>"
                                          :documentPlan  "\"{}\""
                                          :dataSampleRow 0}})]
    (is (nil? errors))
    (is (string? id))
    (is (= "01" uid))
    (is (= "test-updated" name))
    (is (= "<>" blocklyXml))
    (is (= "\"{}\"" documentPlan))
    (is (zero? dataSampleRow))
    (is (pos-int? createdAt))
    (is (pos-int? updatedAt))
    (is (= 1 updateCount))))

(deftest ^:integration delete-document-plan-test
  (is (some? (ops/read! (ops/db-access :blockly) "1")))
  (let [{{{response :deleteDocumentPlan} :data errors :errors} :body}
        (q "/_graphql" :post {:query     "mutation deleteDocumentPlan($id: ID!){deleteDocumentPlan(id: $id)}"
                              :variables {:id "1"}})]
    (is (nil? errors))
    (is (true? response))
    (is (nil? (ops/read! (ops/db-access :blockly) "1")))))
