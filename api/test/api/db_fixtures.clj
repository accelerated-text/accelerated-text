(ns api.db-fixtures
  (:require [api.ddb-fixtures :as ddb]
            [api.datomic-fixtures :as datomic]
            [clojure.tools.logging :as log]))

(defn clean-db [f]
  (when-not (System/getProperty "skip-dynamo")
    (ddb/wipe-ddb-tables f))
  (when-not (System/getProperty "skip-datomic")
    (datomic/datomix-fixture f)))
