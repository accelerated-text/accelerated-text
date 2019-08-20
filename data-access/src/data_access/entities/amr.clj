(ns data-access.entities.amr
  (:require [data-access.db.dynamo-ops :as ops]
            [data-access.utils :as utils]
            [data-access.db.amr-rules :as amr-rules]))

(defn list-verbclasses []
  (concat
   (ops/list! (ops/db-access :verbclass) 100)
   (amr-rules/list-all)))

(defn get-verbclass [k]
  (if (contains? amr-rules/rules (keyword k))
    (get amr-rules/rules (keyword k))
    (ops/read! (ops/db-access :verbclass) k)))

(defn create-verbclass
  [{:keys [id members thematic-roles frames] :as req}]
  (ops/write! (ops/db-access :verbclass) id req))
