(ns data-access.db.s3
  (:require [clojure.tools.logging :as log]
            [data-access.db.config :as config])
  (:import (com.amazonaws.services.s3 AmazonS3Client)
           (com.amazonaws ClientConfiguration)))

(defn build-client
  []
  (let [endpoint (config/s3-endpoint)
        configuration (ClientConfiguration.)
        client (AmazonS3Client. configuration)]
    (when endpoint
      (.setEndpoint client endpoint))
    client))

(defn grant->map
  [grant]
  (let [grantee (.getGrantee grant)
        permission (.getPermission grant)]
    {:permission (.toString permission)
     :grantee (.getIdentifier grantee)}))

(defn summary->map
  [summary]
  (let [key (.getKey summary)]
    {:key key}))

(defn get-acl
  [bucket path]
  (let [acl (.getObjectAcl (build-client) bucket path)
        grants (.getGrantsAsList acl)]
    (map grant->map grants)))

(defn read-file
  [bucket path]
  (let [s3-object (.getObject (build-client) bucket path)
        content (.getObjectContent s3-object)]
    (slurp content)))

(defn list-files
  [bucket path limit]
  (let [resp (.listObjects (build-client) bucket path)
        summary (.getObjectSummaries resp)
        results (map summary->map summary)]
    results))

(defn download-dir
  [bucket path output]
  (let [file-list (list-files bucket path 1000)
        files (map :key file-list)]
    (doseq [f files]
      (log/debugf "Working with: %s" f)
      (let [out-path (clojure.string/join "/" [output f])
            content (read-file bucket f)]
        (println out-path)
        (spit out-path content)))))
