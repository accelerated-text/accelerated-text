(ns lt.tokenmill.nlg.api.data
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.api.resource :as resource])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name lt.tokenmill.nlg.api.DataHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :data))
(def data-bucket "augmented-writer-data-files")

(defn read-data [path-params]
  (let [request-id (path-params :id)
        db (get-db)]
    (utils/do-return ops/read! db request-id)))

(defn add-data [request-body]
  (let [db (get-db)
        parsed-body (doall
                     (utils/csv-to-map request-body))]
    (log/debugf "Parsed body: %s" parsed-body)
    (utils/do-insert (partial ops/write! db) {:data parsed-body})))

(defn delete-data [path-params]
  (let [request-id (path-params :id)
        db (get-db)]
    (utils/do-delete (partial ops/read! db) (partial ops/delete! db) request-id)))

(defn data->listing
  [data]
  (let [first-row (first (data :data))
        header (keys first-row)]
    {:key (data :key)
     :fieldNames header}))

(defn list-data
  [query-params]
  (let [limit (get query-params :limit 20)
        db (get-db)
        data (ops/list! db limit)]
    {:body (map data->listing data)
     :status 200}))


(def -handleRequest
  (resource/build-resource {:get-handler (fn [query-params path-params] (if (empty? path-params)
                                                                          (list-data query-params)
                                                                          (read-data path-params)))
                            :delete-handler delete-data
                            :post-handler add-data}
                           false))
