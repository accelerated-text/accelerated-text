(ns nlg.api.blockly-workspace
  (:require [nlg.api.resource :as resource]
            [nlg.api.utils :as utils]
            [data-access.db.config :as config]
            [data-access.db.dynamo-ops :as ops]
            [taoensso.faraday :as far])
  (:gen-class
    :name nlg.api.WorkspaceHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn write-workspace [body]
  (ops/write! (ops/db-access :blockly) (utils/gen-uuid) body true))

(defn get-workspace
  [path-params]
  (let [key (path-params :id)]
    {:status 200
     :body   (ops/read! (ops/db-access :blockly) key)}))

(defn delete-workspace
  [path-params]
  (let [key (path-params :id)]
    (utils/do-delete (fn [key]
                       (ops/read! (ops/db-access :blockly) key))
                     (fn [key]
                       (ops/delete! (ops/db-access :blockly) key))
                     key)))

(defn add-workspace
  [request-body]
  (utils/do-insert (fn [_ workspace]
                     (write-workspace workspace))
                   request-body))

(defn update-workspace
  [path-params request-body]
  (let [key (path-params :id)]
    (utils/do-update (fn [key workspace]
                       (ops/update! (ops/db-access :blockly) key workspace))
                     key
                     request-body)))

(defn list-workspaces
  [query-params]
  {:body (document-plan/list-document-plans)
   :status 200})

(def -handleRequest
  (resource/build-resource {:get-handler    (fn [query-params path-params] (if (empty? path-params)
                                                                             (list-workspaces query-params)
                                                                             (get-workspace path-params)))
                            :post-handler   add-workspace
                            :delete-handler delete-workspace
                            :put-handler    update-workspace}
                           true))
