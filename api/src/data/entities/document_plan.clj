(ns data.entities.document-plan
  (:require [api.config :refer [conf]]
            [data.db :as db]
            [data.entities.user-group :as user-group]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate document-plans-db :start (db/db-access :document-plan conf))

(defn list-document-plans
  ([group-id]
   (user-group/list-document-plans group-id))
  ([kind group-id]
   (sort-by :name (filter #(= kind (:kind %)) (list-document-plans group-id)))))

(defn get-document-plan [document-plan-id]
  (db/read! document-plans-db document-plan-id))

(defn delete-document-plan [document-plan-id]
  (db/delete! document-plans-db document-plan-id))

(defn add-document-plan
  ([document-plan group-id] (add-document-plan document-plan group-id (or (:id document-plan) (utils/gen-rand-str 16))))
  ([document-plan group-id provided-id]
   (let [plan (db/write! document-plans-db provided-id document-plan true)]
     (user-group/link-document-plan group-id provided-id)
     plan)))

(defn update-document-plan [document-plan-id document-plan]
  (db/update! document-plans-db document-plan-id document-plan))

(defn document-plan-path []
  (get conf :document-plan-path))

(defn load-document-plan [f]
  (let [dp (utils/read-json f)]
    (cond-> dp (string? (:documentPlan dp)) (update :documentPlan utils/read-json-str))))

(defstate document-plans
  :start (doseq [dp (->> (utils/list-files (document-plan-path) #{".json"})
                         (map load-document-plan))]
           (add-document-plan dp user-group/DUMMY-USER-GROUP-ID))
  :stop (doseq [{id :id} (list-document-plans user-group/DUMMY-USER-GROUP-ID)]
          (delete-document-plan id)))
