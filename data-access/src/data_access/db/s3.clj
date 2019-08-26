(ns data-access.db.s3
  (:require [clojure.tools.logging :as log]
            [data-access.db.config :as config]
            [data-access.utils :as utils])
  (:import (com.amazonaws.services.s3 AmazonS3Client)
           (com.amazonaws ClientConfiguration)
           (com.amazonaws.services.s3.model S3ObjectSummary AccessControlList ObjectListing)
           (java.io File)))

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
     :grantee    (.getIdentifier grantee)}))

(defn summary->map
  [^S3ObjectSummary s]
  {:bucket-name   (.getBucketName s)
   :etag          (.getETag s)
   :key           (.getKey s)
   :file-name     (.getName (File. (.getKey s)))
   :last-modified (str (.toInstant (.getLastModified s)))
   :size          (.getSize s)})

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

(defn list-objects
  [bucket path]
  (let [^ObjectListing s3-object-list (.listObjects (build-client) bucket path)]
    (map summary->map (.getObjectSummaries s3-object-list))))

(defn download-dir
  [bucket path output]
  (let [file-list (list-objects bucket path)
        files (map :key file-list)]
    (doseq [f files]
      (log/debugf "Working with: %s" f)
      (let [out-path (clojure.string/join "/" [output f])
            content (read-file bucket f)]
        (println out-path)
        (spit out-path content)))))

(defn read-csv [s3-bucket s3-key]
  (utils/raw-csv->maps (read-file config/data-bucket s3-key)))

(defn get-csv-header [s3-bucket s3-key]
  (-> (read-csv s3-bucket s3-key) (first) (keys) (vec)))
