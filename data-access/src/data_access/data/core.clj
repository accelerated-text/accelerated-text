(ns data-access.data.core
  (:require [data-access.db.config :as config]
            [data-access.db.s3 :as s3]
            [data-access.utils :as utils]))

(defn get-data [user file]
  (let [s3-key (str user "/" file)]
    {:data (s3/read-csv config/data-bucket s3-key)
     :key  s3-key}))

(defn list-data [user limit]
  (let [user (or user "example-user")
        limit (or limit 20)])
  (map (fn [{s3-key :key :as instance}]
         (assoc instance :field-names (s3/get-csv-header config/data-bucket s3-key)))
       (s3/list-files config/data-bucket user limit)))
