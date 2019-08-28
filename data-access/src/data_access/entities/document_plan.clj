(ns data-access.entities.document-plans
  (:require [data-access.db.config :as config]
            [taoensso.faraday :as far]
            [data-access.utils :as utils]))

(defn get-workspace [id]
  (far/get-item (config/client-opts) config/blockly-table {:id id}))

(defn delete-workspace [id]
  (far/delete-item (config/client-opts) config/blockly-table {:id id}))

(defn write-workspace [ws]
  (far/put-item (config/client-opts) config/blockly-table ws))

(defn list-workspaces []
  (far/scan (config/client-opts) config/blockly-table))

(defn add-workspace [ws]
  (write-workspace (assoc ws :createdAt (utils/ts-now))))

(defn update-workspace [ws]
  (->> (assoc ws :updatedAt (utils/ts-now))
       (merge (get-workspace (:id ws)))
       (write-workspace)))
