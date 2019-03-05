(ns lt.tokenmill.nlg.api.lexicon
  (:require [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.api.resource :as resource])
  (:gen-class
    :name lt.tokenmill.nlg.api.LexiconHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :lexicon))

(defn create [request-body]
  (let [db (get-db)]
    {:status 200
     :body   {:request-body request-body}}))

(defn update [path-params request-body]
  (let [db (get-db)]
    {:status 200
     :body   {:path-params  path-params
              :request-body request-body}}))

(defn search [query-params path-params]
  (let [db (get-db)
        result (ops/read! db (get path-params :query))]
    {:status 200
     :body   {:query-params query-params
              :path-params  path-params
              :result       result}}))

(def -handleRequest
  (resource/build-resource {:put-handler  update
                            :post-handler create
                            :get-handler  search}
                           true))
