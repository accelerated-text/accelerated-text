(ns data-access.entities.document-plan
  (:require [data-access.db.config :as config]
            [data-access.db.dynamo-ops :as ops]
            [data-access.utils :as utils]
            [taoensso.faraday :as far]))

(defn list-document-plans []
  (far/scan (config/client-opts) (:table-name config/blockly-table)))

(defn get-document-plan [document-plan-id]
  (ops/read! (ops/db-access :blockly) document-plan-id))

(defn delete-document-plan [document-plan-id]
  (ops/delete! (ops/db-access :blockly) document-plan-id))

(defn add-document-plan [document-plan]
  (ops/write! (ops/db-access :blockly) (utils/gen-uuid) document-plan true))

(defn update-document-plan [document-plan-id document-plan]
  (ops/update! (ops/db-access :blockly) document-plan-id document-plan))
