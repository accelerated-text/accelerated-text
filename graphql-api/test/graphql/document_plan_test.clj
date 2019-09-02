(ns graphql.document-plan-test
  (:require [clojure.test :refer :all]
            [graphql.queries :as queries]
            [graphql.core :as graph]))

(deftest ^:integration document-plans-test
  (queries/validate-resp (graph/nlg (queries/document-plans 0 1))))

(deftest ^:integration document-plan-test
  (queries/validate-resp (graph/nlg (queries/document-plan "17541b7f-eac4-44c4-8e34-12d3af57fd10"))))

(deftest ^:integration create-document-plan-test
  (queries/validate-resp (graph/nlg (queries/create-document-plan {:uid "0"
                                                                   :name "test"
                                                                   :blocklyXml "<>"
                                                                   :documentPlan "{}"}))))

(deftest ^:integration update-document-plan-test
  (queries/validate-resp (graph/nlg (queries/update-document-plan {:id "0"
                                                                   :uid "0"
                                                                   :name "test-updated"
                                                                   :blocklyXml "<>"
                                                                   :documentPlan "{}"}))))

(deftest ^:integration delete-document-plan-test
  (queries/validate-resp (graph/nlg (queries/delete-document-plan "0"))))
