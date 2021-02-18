(ns utils.queries
  (:require [clojure.string :as str]
            [data.utils :refer [list-files]]
            [graphql-builder.core :as core]
            [graphql-builder.parser :refer [defgraphql parse read-file]]))

(defn graphql-file-filter
  [fname]
  (re-matches #".+[.]graphql" fname))

(def graphql-files
  (->> (list-files "resources/")
       (map #(.getName %))
       (filter graphql-file-filter)))

(def query-map
  (->> graphql-files
       (map read-file)
       (str/join "\n")
       (parse)
       (core/query-map)))

(defgraphql doc-plan "export-document-plan.graphql")
(defgraphql doc-plans "export-document-plans.graphql")

(def export-document-plans-query
  (-> doc-plans core/query-map :query :export-document-plans)
  #_(get-in query-map [:query :export-document-plans]))

(def export-document-plan-query (get-in query-map [:query :export-document-plan]))


