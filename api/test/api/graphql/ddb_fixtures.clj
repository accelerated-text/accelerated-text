(ns api.graphql.ddb-fixtures
  (:require [clojure.tools.logging :as log]
            [data.ddb.impl :as ddb]
            [mount.core :as mount]
            [api.server :as server]
            [taoensso.faraday :as far]))

(defn tables [client-ops]
  (far/list-tables client-ops))

(defn create-table! [client-ops {:keys [table-name table-key]}]
  (far/create-table client-ops table-name [table-key :s] {}))

(defn delete-table! [client-ops table-name]
  (far/delete-table client-ops table-name))

(defn wipe-ddb-tables [f]
  (mount/start-without #'server/http-server #'data.datomic.impl/conn)
  (let [client-opts (ddb/client-opts)]
    (doseq [table-name (tables client-opts)]
      (log/tracef "Deleting table: '%s'" (name table-name))
      (delete-table! client-opts table-name))
    (doseq [[_ table-conf] ddb/tables-conf]
      (log/tracef "Creating table: '%s'" table-conf)
      (create-table! client-opts table-conf)))
  (log/debugf "DynamoDB is wiped!")
  (f))
