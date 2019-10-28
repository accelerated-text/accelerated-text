(ns data.entities.result
  (:require [data.db :as db]
            [mount.core :refer [defstate]]))

(defstate results-db :start (db/db-access :results))

(defn store-status
  "Status is a map like {:ready false}"
  [result-id status]
  (db/write! results-db result-id status))

(defn fetch [request-id]
  (db/read! results-db request-id))

(defn delete [request-id]
  (db/delete! results-db request-id))

(defn rewrite [result-id rez]
  (db/update! results-db result-id rez))
