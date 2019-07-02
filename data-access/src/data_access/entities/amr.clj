(ns data-access.entities.amr
  (:require [data-access.db.dynamo-ops :as ops]
            [data-access.utils :as utils]))

(defn list-members [] (ops/list! (ops/db-access :members) 100))

(defn get-member [k] (ops/read! (ops/db-access :members) k))

(defn create-member
  [{:keys [name grouping]}]
  (ops/write! (ops/db-access :members) {:name name
                                        :grouping grouping}))

(defn update-member
  [{:keys [id name grouping]}]
  (ops/update! (ops/db-access :members) id {:id id
                                           :name name
                                           :grouping grouping}))

(defn delete-member [k] (ops/delete! (ops/db-access :members) k))
