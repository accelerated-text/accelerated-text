(ns api.nlg.generate-test
  (:require [api.nlg.generate :refer [generation-process]]
            [api.graphql.ddb-fixtures :as ddb-fixtures]
            [api.test-utils :refer [q]]
            [clojure.test :refer [deftest is use-fixtures]]
            [data.entities.data-files :as data-files]))

(use-fixtures :each ddb-fixtures/wipe-ddb-tables)

(deftest ^:integration basic-generation
  (let [query "mutation createDocumentPlan($uid: ID! $name: String! $blocklyXml: String! $documentPlan: String! $dataSampleRow: Int){createDocumentPlan(uid: $uid name: $name blocklyXml: $blocklyXml documentPlan: $documentPlan dataSampleRow: $dataSampleRow){ id uid name blocklyXml documentPlan dataSampleRow createdAt updatedAt updateCount }}"
        {{{{:keys [id]} :createDocumentPlan} :data} :body}
        (q "/_graphql" :post {:query     query
                              :variables {:uid          "02"
                                          :name         "test"
                                          :blocklyXml   "<>"
                                          :documentPlan "\"{}\""}})
        dataId (data-files/store!
                 {:filename "example-user/data-example.csv"
                  :content  (slurp "resources/accelerated-text-data-files/example-user/data-example.csv")})
        {:keys [results]} (generation-process id dataId nil)]
    (is (= 1 (count results)))))

(deftest ^:integration basic-generation-ccg
  (let [query "mutation createDocumentPlan($uid: ID! $name: String! $blocklyXml: String! $documentPlan: String! $dataSampleRow: Int){createDocumentPlan(uid: $uid name: $name blocklyXml: $blocklyXml documentPlan: $documentPlan dataSampleRow: $dataSampleRow){ id uid name blocklyXml documentPlan dataSampleRow createdAt updatedAt updateCount }}"
        {{{{:keys [id]} :createDocumentPlan} :data} :body}
        (q "/_graphql" :post {:query     query
                              :variables {:uid          "02"
                                          :name         "test"
                                          :blocklyXml   "<>"
                                          :documentPlan "\"{}\""}})
        dataId (data-files/store!
                 {:filename "example-user/ccg-example.csv"
                  :content  (slurp "resources/accelerated-text-data-files/example-user/ccg-example.csv")})
        {:keys [results]} (generation-process id dataId nil)]
    (is (= 1 (count results)))))
