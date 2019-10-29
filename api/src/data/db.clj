(ns data.db
  (:require [data.ddb.impl :as ddb]
            [data.protocol :as protocol]
            [data.utils :as utils]))

(defn read! [db-client key] (protocol/read-item db-client key))
(defn write!
  ([db-client data]
   (protocol/write-item db-client (utils/gen-uuid) data false))
  ([db-client key data]
   (protocol/write-item db-client key data false))
  ([db-client key data update-count?]
   (protocol/write-item db-client key data update-count?)))
(defn update! [db-client key data] (protocol/update-item db-client key data))
(defn delete! [db-client key] (protocol/delete-item db-client key))
(defn list! [db-client limit] (protocol/list-items db-client limit))
(defn scan! [db-client opts] (protocol/scan-items db-client opts))
(defn batch-read! [db-client opts] (protocol/batch-read-items db-client opts))

(defn db-access
  ([resource-type] (db-access resource-type {}))
  ([resource-type config]
   (case (:db-implementation config)
     :dynamodb (ddb/db-access resource-type)
     (ddb/db-access resource-type))))
