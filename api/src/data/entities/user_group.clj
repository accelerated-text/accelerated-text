(ns data.entities.user-group
    (:require [api.config :refer [conf]]
              [data.db :as db]
              [clojure.tools.logging :as log]
              [mount.core :refer [defstate]]
              [data.spec.user-group :as user-group]
              [data.spec.data-file :as data-file]))

(defstate user-group-db :start (db/db-access :user-group conf))

(def DUMMY-USER-GROUP-ID 0)

(defn get-or-create-group [group-id]
  (let [group-info (db/read! user-group-db group-id)]
    (if (not group-info)
      (db/write! user-group-db group-id {::user-group/data-files []
                                         ::user-group/document-plans []})
      group-info)))

(defn link-file [group-id file-ref]
  (let [group-info (get-or-create-group group-id)
        linked-files (get group-info ::user-group/data-files [])]
    (log/debugf "Linking data file: `%s` to user group ID: %d" file-ref group-id)
    (db/update! user-group-db group-id (assoc group-info ::user-group/data-files (conj linked-files [::data-file/id file-ref])))))

(defn link-dp [group-id dp-ref]
  (let [group-info (get-or-create-group group-id)
        linked-plans (get group-info ::user-group/document-plans [])]
    (log/debugf "Linking document plan: `%s` to user group ID: %d" dp-ref group-id)
    (db/update! user-group-db group-id (assoc group-info ::user-group/document-plans (conj linked-plans [:document-plan/id dp-ref])))))

