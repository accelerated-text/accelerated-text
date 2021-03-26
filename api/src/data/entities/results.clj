(ns data.entities.results
  (:require [api.config :refer [conf]]
            [data.db :as db]
            [mount.core :refer [defstate]]))

(defstate results-db :start (db/db-access :results conf))

(defstate results-cache :start (atom {}))

(defn write [result]
  (db/write! results-db nil result))

(defn write-cached-result [hash result-id]
  (swap! results-cache assoc hash result-id))

(defn fetch [result-id]
  (db/read! results-db result-id))

(defn fetch-cached-result [hash]
  (when-let [result-id (get @results-cache hash)]
    (fetch result-id)))

(defn delete [result-id]
  (db/delete! results-db result-id))
