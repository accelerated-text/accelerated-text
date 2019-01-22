(ns lt.tokenmill.nlg.api.data
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.generator.planner :as planner]
            [cheshire.core :as ch])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name lt.tokenmill.nlg.api.DataHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :data))

(defn read-data [path-params]
  (let [request-id (path-params :id)
        db (get-db)]
    {:status 200
     :body (ops/read! db request-id)}))

(defn add-data [request-body]
  (let [db (get-db)]
    {:status 200
     :body (ops/write! db request-body)}))

(defn delete-data [path-params]
  (let [request-id (path-params :id)
        db (get-db)]
    (ops/delete! db request-id)))

(defn -handleRequest [_ is os _]
  (let [input (utils/decode-body is)
        method (input :httpMethod)
        path-params (input :pathParameters)
        query-params (input :queryStringParameters)
        request-body (ch/decode (input :body) true)
        {:keys [status body]} (case (keyword method)
                                :GET    (read-data path-params)
                                :DELETE (delete-data path-params)
                                :POST   (add-data request-body))]
    (log/debugf "Received '%s' and produced output '%s'" input body)
    (with-open [^BufferedWriter w (io/writer os)]
      (.write w ^String (utils/resp 200 body)))))
