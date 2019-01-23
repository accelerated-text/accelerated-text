(ns lt.tokenmill.nlg.api.generate
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.generator.planner :as planner]
            [lt.tokenmill.nlg.api.resource :as resource]
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
        dp (-> (ops/get-workspace dp-id)
               :documentPlan)
        results (utils/result-or-error (map #(planner/render-dp dp %) data))
        body {:ready true
              :results (vec results)}]
    (log/debugf "Body: %s" body)
    (ops/write-results result-id body)
    {:done true}))


(defn generate-request [request-body]
  (let [document-plan-id (request-body :documentPlanId)
        data-id (request-body :dataId)
        result-id (utils/gen-uuid)
        init-results (ops/write-results result-id {:ready false})
        job @(future (generation-process result-id document-plan-id data-id))]
    (utils/do-return (fn [] {:resultId result-id}))))

(defn read-result [path-params]
  (let [request-id (path-params :id)]
    (utils/do-return ops/get-results request-id)))

(defn delete-result [path-params]
  (let [request-id (path-params :id)]
    (utils/do-delete ops/delete-results request-id)))

(def -handleRequest
  (resource/build-resource {:get-handler read-result
                            :post-handler generate-request
                            :delete-handler delete-result}))
