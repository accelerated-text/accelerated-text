(ns data-access.data.core
  (:require [data-access.db.config :as config]
            [data-access.db.s3 :as s3]
            [data-access.utils :as utils]))

(defn get-data [user file])

(defn list-data [{:keys [user limit] :or {user "example-user" limit 20}}]
  (map (fn [{s3-key :key :as instance}]
         (assoc instance :field-names (s3/get-csv-header config/data-bucket s3-key)))
       (s3/list-files config/data-bucket user limit)))
