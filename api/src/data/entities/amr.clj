(ns data.entities.amr
  (:require [data.ddb.amr-rules :as amr-rules]
            [data.db :as db]
            [mount.core :refer [defstate]]))

(defstate verbclass-db :start (db/db-access :verbclass))

(defn list-verbclasses []
  (concat
    (db/list! verbclass-db 100)
    (amr-rules/list-all)))

(defn get-verbclass [k]
  (if (contains? amr-rules/rules (keyword k))
    (get amr-rules/rules (keyword k))
    (db/read! verbclass-db k)))

(defn create-verbclass
  [{:keys [id _ _ _] :as req}]
  (db/write! verbclass-db id req))
