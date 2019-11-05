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

(defn add-instance [instance-id reader-profile instance]
  (db/write! instance-db instance-id {:reader-profile reader-profile :semanticGraphInstance instance}))

(defn update-instance [instance-id reader-profile instance]
  (db/update! instance-db instance-id {:reader-profile reader-profile :semanticGraphInstance instance}))
