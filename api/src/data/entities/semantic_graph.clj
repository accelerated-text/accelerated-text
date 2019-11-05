(ns data.entities.semantic-graph
  (:require [data.db :as db]
            [mount.core :refer [defstate]]))

(defstate instance-db :start (db/db-access :instances))

(defn list-instances []
  (db/scan! instance-db {}))

(defn get-instance [instance-id]
  (db/read! instance-db instance-id))

(defn delete-instance [instance-id]
  (db/delete! instance-db instance-id))

(defn add-instance [instance]
  (db/write! instance-db (:id instance) (dissoc instance :id)))

(defn update-instance [instance]
  (db/update! instance-db (:id instance) (dissoc instance :id)))
