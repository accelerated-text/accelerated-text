(ns utils.queries
  (:require [graphql-builder.parser :refer [parse read-file]]
            [graphql-builder.core :as core]
            [jsonista.core :as json]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.utils :refer [list-files]]))

(defn graphql-file-filter
  [fname]
  (re-matches #"[.]graphql" fname))

(def graphql-files
  (->> (list-files "resources/")
       (map #(.getName %))
       #_(filter graphql-file-filter)))

(def query-map
  (->> graphql-files
       (map read-file)
       (str/join "\n")
       (log/spyf :trace "Content: %s")
       (parse)
       (log/spyf :trace "Parsed: %s")
       (core/query-map)))

(def export-document-plans-query (get-in query-map [:query :export-document-plans]))

(def export-document-plan-query (get-in query-map [:query :export-document-plan]))
