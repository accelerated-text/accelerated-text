(ns data.entities.user-group
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [clojure.tools.logging :as log]
            [data.datomic.entities.document-plan :as dp-entity]
            [data.db :as db]
            [data.spec.user-group :as user-group]
            [data.spec.data-file :as data-file]
            [data.spec.reader-model :as reader-model]
            [mount.core :refer [defstate]]))

(defstate user-group-db :start (db/db-access :user-group conf))

(def DUMMY-USER-GROUP-ID 0)

(defn link-file [group-id file-ref]
  (log/debugf "Linking data file: `%s` to user group ID: %d" file-ref group-id)
  (db/update! user-group-db group-id #::user-group{:data-files [[::data-file/id file-ref]]}))

(defn list-files [group-id]
  (::user-group/data-files
   (db/scan! user-group-db {:group-id group-id :entities #{::user-group/data-files}})))

(defn link-document-plan [group-id dp-ref]
  (log/debugf "Linking document plan: `%s` to user group ID: %d" dp-ref group-id)
  (db/update! user-group-db group-id #::user-group{:document-plans [[:document-plan/id dp-ref]]}))

(defn list-document-plans [group-id]
  (map dp-entity/dp->dp
       (::user-group/document-plans
        (db/scan! user-group-db {:group-id group-id :entities #{::user-group/document-plans}}))))

(defn link-dictionary-item [group-id dict-item-ref]
  (log/debugf "Linking dictionary-item: `%s` to user group ID: %d" dict-item-ref group-id)
  (db/update! user-group-db group-id #::user-group{:dictionary-items [[::dictionary-item/id dict-item-ref]]}))

(defn list-dictionary-items [group-id]
  (::user-group/dictionary-items
   (db/scan! user-group-db {:group-id group-id :entities #{::user-group/dictionary-items}})))

(defn link-reader-model [group-id reader-model-ref]
  (log/debugf "Linking reader model: `%s` to user group ID: %d" reader-model-ref group-id)
  (db/update! user-group-db group-id #::user-group{:reader-models [[::reader-model/code reader-model-ref]]}))

(defn list-reader-models [group-id]
  (::user-group/reader-models
   (db/scan! user-group-db {:group-id group-id :entities #{::user-group/reader-models}})))
