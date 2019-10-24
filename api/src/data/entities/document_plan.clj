(ns data.entities.document-plan
  (:require [data.db.dynamo-ops :as ops]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate document-plans-db :start (ops/db-access :blockly))

(defn list-document-plans []
  (ops/scan! document-plans-db {}))

(defn get-document-plan [document-plan-id]
  (ops/read! document-plans-db document-plan-id))

(defn delete-document-plan [document-plan-id]
  (ops/delete! document-plans-db document-plan-id))

(defn add-document-plan [document-plan]
  (ops/write! document-plans-db (utils/gen-uuid) document-plan true))

(defn update-document-plan [document-plan-id document-plan]
  (ops/update! document-plans-db document-plan-id document-plan))
