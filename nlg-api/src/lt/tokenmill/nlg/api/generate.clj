(ns lt.tokenmill.nlg.api.generate
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.generator.planner :as planner]
            [cheshire.core :as ch])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name lt.tokenmill.nlg.api.NLGHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))


(defn generation-process
  [result-id dp-id data-id]
  (let [data [{"Product name" "Nike Air"
               "Main Feature" "comfort"
               "Secondary feature" "support"
               "Style" "with sleek update on a classic design"
               "Lacing" "premium lacing"}]
        document-plan (-> (ops/get-workspace dp-id)
                          :documentPlan)
        results (utils/result-or-error map #(planner/render-dp dp %) data)
        body (assoc results :ready true)]
    (ops/write-results result-id results)))


(defn generate-request [request-body]
  (let [document-plan-id (log/spyf :info "Document Id: %s" (request-body :documentPlanId))
        data-id (request-body :dataId)
        result-id (utils/gen-uuid)
        init-results (ops/write-results result-id {:ready false})
        job (future (generation-process result-id document-plan-id data-id))]
    {:status 200
     :body {:resultId result-id}}))

(defn read-result [path-params]
  (let [request-id (path-params :id)]
    {:status 200
     :body (ops/get-results request-id)}))

(defn delete-result [path-params]
  (let [request-id (path-params :id)]
    (utils/do-update ops/delete-results request-id)))

(defn -handleRequest [_ is os _]
  (let [input (utils/decode-body is)
        method (input :httpMethod)
        path-params (input :pathParameters)
        query-params (input :queryStringParameters)
        request-body (ch/decode (input :body) true)
        {:keys [status body]} (case (keyword method)
                                :GET    (read-result path-params)
                                :DELETE (delete-result path-params)
                                :POST   (generate-request request-body))]
    (log/debugf "Received '%s' and produced output '%s'" input body)
    (with-open [^BufferedWriter w (io/writer os)]
      (.write w ^String (utils/resp 200 body)))))
