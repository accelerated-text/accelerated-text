(ns api.graphql.document-plan-test
  (:require [api.graphql.core :as graph]
            [api.graphql.queries :as queries]
            [clojure.test :refer [deftest]]
            [data.db.config :as config]))

(def sample-data [{:id            "0"
                   :uid           "0"
                   :name          "test"
                   :blocklyXml    "<>"
                   :documentPlan  "{}"
                   :dataSampleRow 0
                   :createdAt     1568377899
                   :updatedAt     1568377899
                   :updateCount   0}])

(def table-key (:table-key config/blockly-table))

(def sample-data-map (reduce (fn [acc item] (assoc acc (get item table-key) item)) {} sample-data))

(deftest ^:integration document-plans-test
  (with-redefs [taoensso.faraday/scan (fn [& _] sample-data)]
    (queries/validate-resp (graph/nlg (queries/document-plans 0 1)))))

(deftest ^:integration document-plan-test
  (with-redefs [taoensso.faraday/get-item (fn [_ _ prim-kvs & _] (get sample-data-map (get prim-kvs table-key)))]
    (queries/validate-resp (graph/nlg (queries/document-plan "0")))))

(deftest ^:integration create-document-plan-test
  (with-redefs [taoensso.faraday/put-item (fn [& _])]
    (queries/validate-resp (graph/nlg (queries/create-document-plan {:uid          "1"
                                                                     :name         "test"
                                                                     :blocklyXml   "<>"
                                                                     :documentPlan "{}"})))))

(deftest ^:integration update-document-plan-test
  (with-redefs [taoensso.faraday/put-item (fn [& _])
                taoensso.faraday/get-item (fn [_ _ prim-kvs & _] (get sample-data-map (get prim-kvs table-key)))]
    (queries/validate-resp (graph/nlg (queries/update-document-plan {:id            "0"
                                                                     :uid           "0"
                                                                     :name          "test-updated"
                                                                     :blocklyXml    "<>"
                                                                     :documentPlan  "{}"
                                                                     :dataSampleRow 0})))))

(deftest ^:integration delete-document-plan-test
  (with-redefs [taoensso.faraday/delete-item (fn [& _])]
    (queries/validate-resp (graph/nlg (queries/delete-document-plan "0")))))
