(ns data.entities.amr
  (:require [data.db.amr-rules :as amr-rules]
            [data.db.dynamo-ops :as ops]
            [mount.core :refer [defstate]]))

(defstate verbclass-db :start (ops/db-access :verbclass))

(defn list-verbclasses []
  (concat
    (ops/list! verbclass-db 100)
    (amr-rules/list-all)))

(defn get-verbclass [k]
  (if (contains? amr-rules/rules (keyword k))
    (get amr-rules/rules (keyword k))
    (ops/read! verbclass-db k)))

(defn create-verbclass
  [{:keys [id _ _ _] :as req}]
  (ops/write! verbclass-db   id req))
