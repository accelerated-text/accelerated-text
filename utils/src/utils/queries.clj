(ns utils.queries
  (:require [graphql-builder.core :as core]
            [graphql-builder.parser :refer [defgraphql]]
            [jsonista.core :as json]
            [org.httpkit.client :as http]))

(defgraphql doc-plan "export-document-plan.graphql")
(defgraphql doc-plans "export-document-plans.graphql")

(defn run-query [url q]
  @(http/post url {:headers {"Content-Type" "application/json"}
                   :body    (->> q
                                 :graphql
                                 (json/write-value-as-string))}))

(def export-document-plans-query
  (-> doc-plans core/query-map :query :export-document-plans))

(def export-document-plan-query
  (-> doc-plan core/query-map :query :export-document-plan))
