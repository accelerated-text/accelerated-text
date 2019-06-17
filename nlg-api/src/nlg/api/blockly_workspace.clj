(ns nlg.api.blockly-workspace
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [nlg.api.utils :as utils]
            [nlg.db.dynamo-ops :as ops]
            [nlg.api.resource :as resource]
            [cheshire.core :as ch])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name nlg.api.WorkspaceHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-workspace
  [path-params]
  (let [key (path-params :id)]
    {:status 200
     :body (ops/get-workspace key)}))

(defn delete-workspace
  [path-params]
  (let [key (path-params :id)]
    (utils/do-delete ops/get-workspace ops/delete-workspace key)))

(defn add-workspace
  [request-body]
  (utils/do-insert ops/add-workspace request-body))

(defn update-workspace
  [path-params request-body]
  (let [key (path-params :id)]
    (utils/do-update ops/update-workspace key request-body)))

(defn list-workspaces
  [query-params]
  (let [limit (get query-params :limit 20)]
    {:body (ops/list-workspaces limit)
     :status 200}))

(def -handleRequest
  (resource/build-resource {:get-handler (fn [query-params path-params] (if (empty? path-params)
                                                                          (list-workspaces query-params)
                                                                          (get-workspace path-params)))
                            :post-handler add-workspace
                            :delete-handler delete-workspace
                            :put-handler update-workspace}
                           true))
