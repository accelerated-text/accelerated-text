(ns data-access.entities.document-plan
  (:require [data-access.db.config :as config]
            [data-access.db.dynamo-ops :as ops]
            [taoensso.faraday :as far]))

(defn list []
  (far/scan (config/client-opts) config/blockly-table))

(defn get [document-plan-id]
  (ops/read! (ops/db-access config/blockly-table) document-plan-id))

(defn delete [document-plan-id]
  (ops/delete! (ops/db-access config/blockly-table) document-plan-id))

(defn add [document-plan-id document-plan]
  (ops/write! (ops/db-access config/blockly-table) document-plan-id document-plan true))

(defn update [document-plan-id document-plan]
  (ops/update! (ops/db-access config/blockly-table) document-plan-id document-plan))
