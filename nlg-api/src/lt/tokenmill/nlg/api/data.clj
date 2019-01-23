(ns lt.tokenmill.nlg.api.data
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.api.resource :as resource]
            [cheshire.core :as ch])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name lt.tokenmill.nlg.api.DataHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :data))

(defn read-data [path-params]
  (let [request-id (path-params :id)
        db (get-db)]
    (utils/do-return ops/read! db request-id)))

(defn add-data [request-body]
  (let [db (get-db)]
    (utils/do-insert ops/write! db request-body)))

(defn delete-data [path-params]
  (let [request-id (path-params :id)
        db (get-db)]
    (utils/do-delete (partial ops/read! db) (partial ops/delete! db) request-id)))


(def -handleRequest
  (resource/build-resource {:get-handler read-data
                            :delete-handler delete-data
                            :post add-data}
                           false))
