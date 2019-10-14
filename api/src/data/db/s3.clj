(ns data.db.s3
  (:require [cognitect.aws.client.api :as aws]
            [cognitect.aws.credentials :as credentials]
            [data.db.config :as config])
  (:import (java.net URI)))

(def s3
  (delay
    (let [endpoint (some-> (config/s3-endpoint) (URI.))]
      (aws/client (cond-> {:api                  :s3
                           :credentials-provider (credentials/default-credentials-provider
                                                   (aws/default-http-client))}
                          (some? endpoint) (assoc :endpoint-override {:protocol (.getScheme endpoint)
                                                                      :hostname (.getHost endpoint)
                                                                      :port     (.getPort endpoint)}))))))

(defn- summary->map [bucket item]
  {:bucket-name   bucket
   :etag          (:ETag item)
   :key           (:Key item)
   :last-modified (inst-ms (:LastModified item))
   :size          (:Size item)})

(defn list-objects [bucket prefix]
  (map (partial summary->map bucket)
       (-> @s3
           (aws/invoke {:op      :ListObjects
                        :request {:Bucket bucket :Prefix prefix}})
           (get :Contents))))

(defn read-file [bucket key]
  (some-> @s3
          (aws/invoke {:op      :GetObject
                       :request {:Bucket bucket :Key key}})
          (get :Body)
          (slurp)))
