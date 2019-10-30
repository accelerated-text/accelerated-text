(ns api.db-fixtures
  (:require [api.ddb-fixtures :as ddb]
            [api.datomic-fixtures :as datomic]))

(defn clean-db [f]
  (when-not (System/getProperty "skip-dynamo")
    (ddb/wipe-ddb-tables f))
  (when-not (System/getProperty "skip-datomic")
    (datomic/datomix-fixture f)))
