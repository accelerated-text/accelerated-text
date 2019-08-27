(ns data-access.db.s3
  (:require [clojure.tools.logging :as log]
            [data-access.db.config :as config]
            [data-access.utils :as utils])
  (:import (java.io File)
           (com.amazonaws ClientConfiguration)
           (com.amazonaws.services.s3 AmazonS3Client)
           (com.amazonaws.services.s3.model AccessControlList Grant ObjectListing S3Object S3ObjectSummary)))

(defn build-client []
  (let [endpoint (config/s3-endpoint)]
    (cond-> (AmazonS3Client. (ClientConfiguration.))
            (some? endpoint) (.setEndpoint endpoint))))

(defn- grant->map [^Grant g]
  {:permission (str (.getPermission g))
   :grantee    (.getIdentifier (.getGrantee g))})

(defn get-acl [bucket path]
  (map grant->map (-> (build-client)
                      (.getObjectAcl bucket path)
                      (.getGrantsAsList))))

(defn- summary->map [^S3ObjectSummary s]
  {:bucket-name   (.getBucketName s)
   :etag          (.getETag s)
   :key           (.getKey s)
   :last-modified (inst-ms (.getLastModified s))
   :size          (.getSize s)})

(defn list-objects [bucket path]
  (map summary->map (-> (build-client)
                        (.listObjects bucket path)
                        (.getObjectSummaries))))

(defn read-file [bucket path]
  (-> (build-client)
      (.getObject bucket path)
      (.getObjectContent)
      (slurp)))

(defn download-dir [bucket path output-dir]
  (doseq [{file :key} (list-objects bucket path)]
    (let [output-path (clojure.string/join "/" [output-dir file])
          content (read-file bucket file)]
      (log/debugf "Writing file: %s" output-path)
      (spit output-path content))))
