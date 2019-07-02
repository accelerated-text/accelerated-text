(ns data-access.entities.amr
  (:require [data-class.db.dynamo-ops :as ops]
            [data-access.utils :as utils]))

(defn list-members [] (ops/list! (ops/db-access :members) 100))

(defn get-member [k] (ops/read! (ops/db-access :members) k))

(defn update-member [{:keys [id name grouping]}])

(defn delete-member [k])
