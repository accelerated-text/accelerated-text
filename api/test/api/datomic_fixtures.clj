(ns api.datomic-fixtures
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]
            [clojure.java.io :as io])
  (:import (java.util UUID)))

(defn scratch-conn
  "Creates an in-memory Datomic connection.
  NOTE: we actually won't be using this implementation, see next section on forking connections."
  []
  (let [uri (str "datomic:mem://" "mem-conn-" (UUID/randomUUID))]
    (d/create-database uri)
    (d/connect uri)))

(defn migrate [conn]
  (c/ensure-conforms conn (c/read-resource "datomic-schema/2019-10-28-initial-schema.edn")))

(defn datomix-fixture [f]
  (let [conn (scratch-conn)]
    (migrate conn)
    (f)))
