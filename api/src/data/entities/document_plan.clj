(ns data.entities.document-plan
  (:require [data.db.dynamo-ops :as ops]
            [data.utils :as utils]))

(defn list-document-plans []
  (ops/scan! (ops/db-access :blockly) {}))

(defn get-document-plan [document-plan-id]
  (ops/read! (ops/db-access :blockly) document-plan-id))

(defn delete-document-plan [document-plan-id]
  (ops/delete! (ops/db-access :blockly) document-plan-id))

(defn add-document-plan [document-plan]
  (ops/write! (ops/db-access :blockly) (utils/gen-uuid) document-plan true))

(defn update-document-plan [document-plan-id document-plan]
  (ops/update! (ops/db-access :blockly) document-plan-id document-plan))
