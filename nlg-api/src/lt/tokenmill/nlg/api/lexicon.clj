(ns lt.tokenmill.nlg.api.lexicon
  (:require [lt.tokenmill.nlg.api.utils :as utils]
            [lt.tokenmill.nlg.db.dynamo-ops :as ops]
            [lt.tokenmill.nlg.api.resource :as resource]
            [clojure.string :as str])
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

(defn process-search-response [resp offset limit]
  (let [offset (max 0 (Integer/parseInt (or offset "0")))
        limit (max 0 (Integer/parseInt (or limit "15")))
        count (count resp)]
    {:offset     offset
     :totalCount count
     :items      (subvec resp (min count offset) (min count (+ offset limit)))}))

(defn search [{:keys [query offset limit] :as query-params} path-params]
  (let [db (get-db)]
    (utils/do-return
      (comp (fn [resp]
              (when (seq resp) (process-search-response resp offset limit)))
            (partial ops/scan! db))
      {:attr-conds {:word [:begins-with query]}})))

(def -handleRequest
  (resource/build-resource {:put-handler  update
                            :post-handler create
                            :get-handler  search}
                           true))
