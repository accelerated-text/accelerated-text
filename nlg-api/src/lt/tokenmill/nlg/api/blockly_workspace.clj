(ns lt.tokenmill.nlg.api.blockly-workspace
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [cheshire.core :as ch])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name lt.tokenmill.nlg.api.WorkspaceHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-workspace
  [path-params]
  (let [key (path-params :id)]
    {:status 200
     :body (ops/get-workspace key)}))

(defn delete-workspace
  [path-params]
  (let [key (path-params :id)]
    (utils/do-update ops/delete-workspace key)))

(defn add-workspace
  [request-body]
  (utils/do-insert ops/add-workspace request-body))

(defn update-workspace
  [path-params request-body]
  (let [key (path-params :id)]
    (utils/do-update ops/update-workspace key request-body)))

(defn -handleRequest [_ is os _]
  (let [input (utils/decode-body is)
        method (input :httpMethod)
        path-params (input :pathParameters)
        request-body (ch/decode (input :body))
        {:keys [status body]} (case method
                                "GET"    (get-workspace path-params)
                                "DELETE" (delete-workspace path-params)
                                "POST"   (add-workspace request-body)
                                "PUT"    (update-workspace path-params request-body))]
    (log/debugf "Received '%s' and produced output '%s'" input body)
    (with-open [^BufferedWriter w (io/writer os)]
      (.write w ^String (utils/resp status body)))))
