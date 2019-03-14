(ns lt.tokenmill.nlg.db.s3
  (:import (com.amazonaws.services.s3 AmazonS3Client)
           (com.amazonaws ClientConfiguration)))

(def client
  (let [configuration (-> (ClientConfiguration.))]
    (AmazonS3Client. configuration)))

(defn read-file [bucket path]
  (let [s3-object (.getObject client bucket path)
        content (.getObjectContent s3-object)]
    (slurp content)))
