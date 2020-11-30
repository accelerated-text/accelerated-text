(ns api.db-fixtures
  (:require [api.datomic-fixtures :as datomic]))

(defn clean-db [f]
  (when-not (System/getProperty "skip-datomic")
    (datomic/datomic-fixture f)))
