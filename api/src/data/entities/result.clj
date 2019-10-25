(ns data.entities.result
  (:require [data.db :as ops]
            [mount.core :refer [defstate]]))

(defstate results-db :start (ops/db-access :results))

(defn store-status
  "Status is a map like {:ready false}"
  [result-id status]
  (ops/write! results-db result-id status))

(defn fetch [request-id]
  (ops/read! results-db request-id))

(defn delete [request-id]
  (ops/delete! results-db request-id))

(defn update [result-id rez]
  (ops/update! results-db result-id rez))
