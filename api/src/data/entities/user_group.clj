(ns data.entities.user-group
    (:require [api.config :refer [conf]]
              [data.db :as db]
              [clojure.tools.logging :as log]
              [mount.core :refer [defstate]]))

(defstate user-group-db :start (db/db-access :user-group conf))

(def DUMMY-USER-GROUP-ID 0)

(defn get-or-create-group [group-id]
  (let [group-info (db/read! user-group-db group-id)]
    (if (not group-info)
      (db/write! user-group-db group-id {})
      group-info)))

(defn link-file [group-id file-ref]
  (let [group-info (get-or-create-group group-id)
        linked-files (get group-info :data-files [])]
    (log/infof "Linking data file: `%s` to user group ID: %d" file-ref group-id)
    (db/update! user-group-db group-id (assoc group-info :data-files (conj linked-files file-ref)))))

