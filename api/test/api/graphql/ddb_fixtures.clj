(ns api.graphql.ddb-fixtures
  (:require [clojure.tools.logging :as log]
            [data.db.config :as config]
            [data.db.dynamo-ops :as ops]
            [mount.core :as mount]
            [api.server :as server]
            [taoensso.faraday :as far]))

(defn tables []
  (far/list-tables (config/client-opts)))

(defn create-table! [{:keys [table-name table-key]}]
  (far/create-table (config/client-opts) table-name [table-key :s] {}))

(defn delete-table! [table-name]
  (far/delete-table (config/client-opts) table-name))

(defn wipe-ddb-tables [f]
  (mount/start-without #'server/http-server)
  (doseq [table-name (tables)]
   (log/tracef "Deleting table: '%s'" (name table-name))
   (delete-table! table-name))
  (doseq [[_ table-conf] ops/tables-conf]
    (log/tracef "Creating table: '%s'" table-conf)
    (create-table! table-conf))
  (log/debugf "DynamoDB is wiped!")
  (f))
