(ns lt.tokenmill.nlg.db.dynamo-ops
  (:require [taoensso.faraday :as far]
            [lt.tokenmill.nlg.db.config :as config]
            [lt.tokenmill.nlg.api.utils :as utils]))

(defn get-workspace
  [key]
  (far/get-item config/client-opts config/blockly-table {:id key}))

(defn list-workspaces
  [limit]
  (far/scan config/client-opts config/blockly-table {:limit limit}))


(defn write-workspace
  [key workspace]
  (let [body (assoc workspace :id key)]
    (do
      (far/put-item
       config/client-opts
       config/blockly-table
       body)
      body)))

(defn add-workspace
  [key workspace]
  (let [body (assoc workspace :createdAt (utils/ts-now))]
    (write-workspace key body)))

(defn update-workspace
  [key workspace]
  (let [original (get-workspace key)
        body (merge
              original
              (assoc workspace :updatedAt (utils/ts-now)))]
    (write-workspace key body)))
  

(defn delete-workspace
  [key]
  (far/delete-item config/client-opts config/blockly-table {:id key}))
