(ns data.entities.document-plan
  (:require [api.config :refer [conf]]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate document-plans-db :start (db/db-access :document-plan conf))

(defn list-document-plans
  ([]
   (db/scan! document-plans-db {}))
  ([kind]
   (sort-by :name (filter #(= kind (:kind %)) (db/scan! document-plans-db {})))))

(defn get-document-plan [document-plan-id]
  (db/read! document-plans-db document-plan-id))

(defn delete-document-plan [document-plan-id]
  (db/delete! document-plans-db document-plan-id))

(defn add-document-plan
  ([document-plan] (add-document-plan document-plan (or (:id document-plan) (utils/gen-rand-str 16))))
  ([document-plan provided-id]
   (db/write! document-plans-db provided-id document-plan true)))

(defn update-document-plan [document-plan-id document-plan]
  (db/update! document-plans-db document-plan-id document-plan))

(defn document-plan-path []
  (get conf :document-plan-path))

(defn load-document-plan [f]
  (let [dp (utils/read-json f)]
    (cond-> dp (string? (:documentPlan dp)) (update :documentPlan utils/read-json-str))))

(defn initialize []
  (doseq [{id :id :as dp}
          (->> (utils/list-files (document-plan-path) #{".json"})
               (map load-document-plan))]
    (add-document-plan dp id)))
