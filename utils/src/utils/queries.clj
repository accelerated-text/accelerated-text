(ns utils.queries
  (:require [graphql-builder.core :as core]
            [graphql-builder.parser :refer [defgraphql]]))

(defgraphql doc-plan "export-document-plan.graphql")
(defgraphql doc-plans "export-document-plans.graphql")

(def export-document-plans-query
  (-> doc-plans core/query-map :query :export-document-plans))

(def export-document-plan-query
  (-> doc-plan core/query-map :query :export-document-plan))
