(ns lt.tokenmill.nlg.api.generate
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [cheshire.core :as ch])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name lt.tokenmill.nlg.api.NLGHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))


(defn generate-request [request-body]
  (let [document-plan-id (request-body :documentPlanId)
        data-id (request-body :dataId)
        document-plan (ops/get-workspace document-plan-id)]
    {:status 200
     :body {:resultId "123A"}}))

(defn read-result [path-params]
  (let [request-id (path-params :id)]
    {:status 200
     :body {:ready false}}))

(defn delete-result [path-params]
  (let [request-id (path-params :id)]
    {:status 200
     :body {:success true}}))

(defn -handleRequest [_ is os _]
  (let [input (utils/decode-body is)
        method (input :httpMethod)
        path-params (input :pathParameters)
        query-params (input :queryStringParameters)
        request-body (ch/decode (input :body))
        {:keys [status body]} (case (keyword method)
                                :GET    (read-result path-params)
                                :DELETE (delete-result path-params)
                                :POST   (generate-request request-body))]
    (log/debugf "Received '%s' and produced output '%s'" input body)
    (with-open [^BufferedWriter w (io/writer os)]
      (.write w ^String (utils/resp 200 body)))))
