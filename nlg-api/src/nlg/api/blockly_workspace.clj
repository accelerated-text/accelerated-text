(ns nlg.api.blockly-workspace
  (:require [nlg.api.resource :as resource]
            [nlg.api.utils :as utils]
            [data-access.db.config :as config]
            [taoensso.faraday :as far])
  (:gen-class
    :name nlg.api.WorkspaceHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn write-workspace [body]
  (far/put-item (config/client-opts) (:table-name config/blockly-table) body)
  body)

(defn get-workspace
  [path-params]
  (let [key (path-params :id)]
    {:status 200
     :body   (far/get-item (config/client-opts) (:table-name config/blockly-table) {:id key})}))

(defn delete-workspace
  [path-params]
  (let [key (path-params :id)]
    (utils/do-delete (fn [key]
                       (far/get-item (config/client-opts) (:table-name config/blockly-table) {:id key}))
                     (fn [key]
                       (far/delete-item (config/client-opts) (:table-name config/blockly-table) {:id key}))
                     key)))

(defn add-workspace
  [request-body]
  (utils/do-insert (fn [key workspace]
                     (write-workspace (assoc workspace :id key :createdAt (utils/ts-now))))
                   request-body))

(defn update-workspace
  [path-params request-body]
  (let [key (path-params :id)]
    (utils/do-update (fn [key workspace]
                       (write-workspace (merge (get-workspace key) (assoc workspace :updatedAt (utils/ts-now)))))
                     key
                     request-body)))

(defn list-workspaces
  [query-params]
  (let [limit (get query-params :limit 20)]
    {:body   (far/scan (config/client-opts) (:table-name config/blockly-table) {:limit limit})
     :status 200}))

(def -handleRequest
  (resource/build-resource {:get-handler    (fn [query-params path-params] (if (empty? path-params)
                                                                             (list-workspaces query-params)
                                                                             (get-workspace path-params)))
                            :post-handler   add-workspace
                            :delete-handler delete-workspace
                            :put-handler    update-workspace}
                           true))
