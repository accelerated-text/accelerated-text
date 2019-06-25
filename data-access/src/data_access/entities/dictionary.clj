(ns data-access.entities.dictionary
  (:require [data-access.db.dynamo-ops :as ops]
            [data-access.db.config :as config]))

(defn list-readers
  []
  (let [db (ops/db-access :reader-flag)]
    (ops/list! db 100)))

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

(defn create-dictionary-item
  [{:keys [key phrases partOfSpeech]}]
  (let [db (ops/db-access :dictionary-combined)
        readers (list-readers)
        default-flags (into {} (map (fn [r]  {(keyword (r :id)) :DONT_CARE}) readers))
        result {:name key
                :partOfSpeech partOfSpeech
                :phrases (map (fn [p] {:text p
                                       :flags (assoc default-flags :default :YES)}) phrases)}]
    (ops/write! db key result)))

(defn update-dictionary-item
  [{:keys [key phrases]}]
  (let [db (ops/db-access :dictionary-combined)]
    (ops/update! db key {:phrases phrases})))


