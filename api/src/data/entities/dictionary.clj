(ns data.entities.dictionary
  (:require [api.config :refer [conf]]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate reader-flags-db :start (db/db-access :reader-flag conf))
(defstate dictionary-combined-db :start (db/db-access :dictionary-combined conf))

(defn list-readers []
  (db/list! reader-flags-db 100))

(defn get-default-flags []
  (->> (list-readers)
       (map (fn [r] {(keyword (:id r)) :DONT_CARE}))
       (into {})))

(defn get-reader [key]
  (db/read! reader-flags-db key))

(defn list-dictionary []
  (db/list! dictionary-combined-db 100))

(defn get-dictionary-item [key]
  (when-not (str/blank? key)
    (db/read! dictionary-combined-db key)))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (get-default-flags)))
  ([text parent-id default-usage default-flags]
   {:id    (format "%s/%s" parent-id (utils/gen-uuid))
    :text  text
    :flags (assoc default-flags :default default-usage)}))

(defn create-dictionary-item [{:keys [key name phrases partOfSpeech]}]
  (when-not (str/blank? name)
    (db/write! dictionary-combined-db key {:name         name
                                           :partOfSpeech partOfSpeech
                                           :phrases      (map #(text->phrase % key :YES) phrases)})))

(defn delete-dictionary-item [key]
  (db/delete! dictionary-combined-db key))

(defn update-dictionary-item [item]
  (db/update! dictionary-combined-db (:key item) (dissoc item :key)))
