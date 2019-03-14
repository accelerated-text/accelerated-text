(ns lt.tokenmill.nlg.db.s3
  (:import (com.amazonaws.services.s3 AmazonS3Client AmazonS3ClientBuilder)
           (com.amazonaws ClientConfiguration)
           (com.amazonaws.services.s3.model ListObjectsV2Request ListObjectsV2Result)))

(def client
  (let [configuration (-> (ClientConfiguration.))]
    (AmazonS3Client. configuration)))

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
  (let [acl (.getObjectAcl client bucket path)
        grants (.getGrantsAsList acl)]
    (map grant->map grants)))

(defn read-file
  [bucket path]
  (let [s3-object (.getObject client bucket path)
        content (.getObjectContent s3-object)]
    (slurp content)))

(defn list-files
  [bucket path limit]
  (let [req (-> (ListObjectsV2Request.)
                (.withBucketName bucket)
                (.withPrefix path)
                (.withMaxKeys (int limit)))
        resp (.listObjectsV2 client req)
        summary (.getObjectSummaries resp)
        results (map summary->map summary)]
    results))
