(ns data.entities.results
  (:require [api.config :refer [conf]]
            [data.db :as db]
            [mount.core :refer [defstate]]))

(defstate results-db :start (db/db-access :results conf))

(defn write [result]
  (db/write! results-db nil result))

(defn fetch [result-id]
  (db/read! results-db result-id))

(defn delete [result-id]
  (db/delete! results-db result-id))
