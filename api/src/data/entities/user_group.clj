(ns data.entities.user-group
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [data.db :as db]
            [clojure.tools.logging :as log]
            [mount.core :refer [defstate]]
            [data.spec.user-group :as user-group]
            [data.spec.data-file :as data-file]
            [data.spec.reader-model :as reader-model]))

(defstate user-group-db :start (db/db-access :user-group conf))

(def DUMMY-USER-GROUP-ID 0)

(defn get-or-create-group [group-id]
  (let [group-info (db/read! user-group-db group-id)]
    (if (not group-info)
      (db/write! user-group-db group-id {::user-group/data-files       []
                                         ::user-group/document-plans   []
                                         ::user-group/dictionary-items []
                                         ::user-group/reader-models    []})
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

(defn link-dict-item [group-id dict-item-ref]
  (let [group-info (get-or-create-group group-id)
        linked-plans (get group-info ::user-group/dictionary-items [])]
    (log/debugf "Linking dictionary-item: `%s` to user group ID: %d" dict-item-ref group-id)
    (db/update! user-group-db group-id (assoc group-info ::user-group/dictionary-items (conj linked-plans [::dictionary-item/id dict-item-ref])))))

(defn link-reader-model [group-id reader-model-ref]
  (let [group-info (get-or-create-group group-id)
        linked-plans (get group-info ::user-group/reader-models [])]
    (log/debugf "Linking reader model: `%s` to user group ID: %d" reader-model-ref group-id)
    (db/update! user-group-db group-id (assoc group-info ::user-group/reader-models (conj linked-plans [::reader-model/code reader-model-ref])))))
