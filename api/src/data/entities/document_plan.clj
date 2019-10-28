(ns data.entities.document-plan
  (:require [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate document-plans-db :start (db/db-access :blockly))

(defn list-document-plans []
  (db/scan! document-plans-db {}))

(defn get-document-plan [document-plan-id]
  (db/read! document-plans-db document-plan-id))

(defn delete-document-plan [document-plan-id]
  (db/delete! document-plans-db document-plan-id))

(defn add-document-plan
  ([document-plan] (add-document-plan document-plan (utils/gen-uuid)))
  ([document-plan provided-id]
   (db/write! document-plans-db provided-id document-plan true)))

(defn update-document-plan [document-plan-id document-plan]
  (db/update! document-plans-db document-plan-id document-plan))
