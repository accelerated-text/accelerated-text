(ns data-access.entities.amr
  (:require [data-access.db.dynamo-ops :as ops]
            [data-access.utils :as utils]))

(defn list-verbclasses [] (ops/list! (ops/db-access :verbclass) 100))

(defn get-verbclass [k] (ops/read! (ops/db-access :verbclass) k))

(defn create-verbclass
  [{:keys [id members thematic-roles frames] :as req}]
  (ops/write! (ops/db-access :verbclass) id req))
