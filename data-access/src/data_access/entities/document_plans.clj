(ns data-access.entities.document-plans
  (:require [data-access.db.config :as config]
            [taoensso.faraday :as far]
            [data-access.utils :as utils]))

(defn get-workspace [key]
  (far/get-item (config/client-opts) config/blockly-table {:id key}))

(defn delete-workspace [key]
  (far/delete-item (config/client-opts) config/blockly-table {:id key}))

(defn write-workspace [key workspace]
  (far/put-item (config/client-opts) config/blockly-table (assoc workspace :id key)))

(defn list-workspaces [limit]
  (far/scan (config/client-opts) config/blockly-table {:limit limit}))

(defn add-workspace [key workspace]
  (write-workspace key (assoc workspace :createdAt (utils/ts-now))))

(defn update-workspace [key workspace]
  (write-workspace key (merge (get-workspace key) (assoc workspace :updatedAt (utils/ts-now)))))
