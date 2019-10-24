(ns data.entities.dictionary
  (:require [clojure.string :as str]
            [data.db.dynamo-ops :as ops]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate reader-flags-db :start (ops/db-access :reader-flag))
(defstate dictionary-compined-db :start (ops/db-access :dictionary-combined))

(defn list-readers []
  (ops/list! reader-flags-db 100))

(defn get-default-flags []
  (->> (list-readers)
       (map (fn [r] {(keyword (:id r)) :DONT_CARE}))
       (into {})))

(defn get-reader [key]
  (ops/read! reader-flags-db key))

(defn list-dictionary []
  (ops/list! dictionary-compined-db 100))

(defn get-dictionary-item [key]
  (when-not (str/blank? key)
    (ops/read! dictionary-compined-db key)))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (get-default-flags)))
  ([text parent-id default-usage default-flags]
   {:id    (format "%s/%s" parent-id (utils/gen-uuid))
    :text  text
    :flags (assoc default-flags :default default-usage)}))

(defn create-dictionary-item [{:keys [key name phrases partOfSpeech]}]
  (when-not (str/blank? name)
    (ops/write! dictionary-compined-db key {:name         name
                                            :partOfSpeech partOfSpeech
                                            :phrases      (map #(text->phrase % key :YES) phrases)})))

(defn delete-dictionary-item [key]
  (ops/delete! dictionary-compined-db key))

(defn update-dictionary-item [item]
  (ops/update! dictionary-compined-db (:key item) (dissoc item :key)))
