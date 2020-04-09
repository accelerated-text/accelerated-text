(ns utils.queries
  (:require [graphql-builder.parser :refer [parse read-file]]
            [graphql-builder.core :as core]
            [jsonista.core :as json]
            [clojure.string :as str]
            [data.utils :refer [list-files]]))

(defn graphql-file-filter
  [fname]
  (re-matches #"[.]graphql" fname))

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

(def export-document-plans-query (get-in query-map [:query :export-document-plans]))
