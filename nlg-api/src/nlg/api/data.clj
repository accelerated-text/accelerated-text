(ns nlg.api.data
  (:require [nlg.api.utils :as utils]
            [data-access.db.dynamo-ops :as ops]
            [data-access.db.config :as config]
            [nlg.api.resource :as resource]
            [data-access.db.s3 :as s3])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name nlg.api.DataHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :data))

(defn read-data
  [path-params]
  (let [user (path-params :user)
        filename (path-params :file)
        key (clojure.string/join "/" [user filename])
        raw (s3/read-file config/data-bucket key)
        csv (doall (utils/csv-to-map raw))]
    {:body {:data csv
            :key key}
     :status 200}))

(defn data->listing
  [data]
  (let [first-row (first (data :data))
        header (keys first-row)]
    {:key (data :key)
     :fieldNames header}))

(defn file->data
  [file]
  (let [raw (s3/read-file config/data-bucket (file :key))
        csv (utils/csv-to-map raw)]
    (assoc file :data csv)))

(defn list-data
  [query-params]
  (let [limit (get query-params :limit 20)
        user (get query-params :user "default")
        files (s3/list-objects config/data-bucket user)
        data (map file->data (take limit files))]
    {:body (map data->listing data)
     :status 200}))

(def -handleRequest
  (resource/build-resource {:get-handler (fn [query-params path-params] (if (empty? path-params)
                                                                          (list-data query-params)
                                                                          (read-data path-params)))}
                           false))
