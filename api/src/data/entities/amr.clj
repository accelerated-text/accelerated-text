(ns data.entities.amr
  (:require [data.db.amr-rules :as amr-rules]
            [data.db.dynamo-ops :as ops]))

(defn list-verbclasses []
  (concat
    (ops/list! (ops/db-access :verbclass) 100)
    (amr-rules/list-all)))

(defn get-verbclass [k]
  (if (contains? amr-rules/rules (keyword k))
    (get amr-rules/rules (keyword k))
    (ops/read! (ops/db-access :verbclass) k)))

(defn create-verbclass
  [{:keys [id _ _ _] :as req}]
  (ops/write! (ops/db-access :verbclass) id req))
