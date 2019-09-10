(ns nlg.api.blockly-workspace
  (:require [nlg.api.resource :as resource]
            [nlg.api.utils :as utils]
            [data-access.db.config :as config]
            [taoensso.faraday :as far]
            [data-access.entities.document-plan :as document-plan])
  (:gen-class
    :name nlg.api.WorkspaceHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn write-workspace [body]
  (far/put-item (config/client-opts) (:table-name config/blockly-table) body)
  body)

(defn get-workspace
  [{:keys [id]}]
  {:status 200
   :body (document-plan/get-document-plan id)})

(defn delete-workspace
  [{:keys [id]}]
  (let [original (document-plan/get-document-plan id)]
    (document-plan/delete-document-plan id)
    {:status 200
     :body original}))

(defn add-workspace
  [dp]
  (document-plan/add-document-plan dp))

(defn update-workspace
  [{:keys [id]} dp]
  (document-plan/update-document-plan id dp)
  (get-workspace id))

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
