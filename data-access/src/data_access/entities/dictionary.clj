(ns data-access.entities.dictionary
  (:require [data-access.db.dynamo-ops :as ops]
            [data-access.db.config :as config]
            [data-access.utils :as utils]))

(defn list-readers
  []
  (let [db (ops/db-access :reader-flag)]
    (ops/list! db 100)))

(defn get-default-flags
  []
  (into {} (map (fn [r]  {(keyword (r :id)) :DONT_CARE}) (list-readers))))

(defn get-reader
  [k]
  (let [db (ops/db-access :reader-flag)]
    (ops/read! db k)))

(defn list-dictionary
  []
  (let [db (ops/db-access :dictionary-combined)]
    (ops/list! db 100)))

(defn get-dictionary-item
  [k]
  (let [db (ops/db-access :dictionary-combined)]
    (ops/read! db k)))

(defn text->phrase
  [text default-usage default-flags]
  {:id (utils/gen-uuid)
   :text text
   :flags (assoc default-flags :default default-usage)})

(defn create-dictionary-item
  [{:keys [key phrases partOfSpeech]}]
  (let [db (ops/db-access :dictionary-combined)
        default-flags (get-default-flags)
        result {:name key
                :partOfSpeech partOfSpeech
                :phrases (doall (map #(text->phrase % :YES default-flags) phrases))}]
    (ops/write! db key result)))

(defn update-dictionary-item
  [{:keys [key phrases partOfSpeech]}]
  (let [db (ops/db-access :dictionary-combined)]
    (ops/update! db key {:phrases phrases
                         :partOfSpeech partOfSpeech})))


