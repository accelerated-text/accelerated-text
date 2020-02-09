(ns data.entities.dictionary
  (:require [api.config :refer [conf]]
            [clj-yaml.core :as yaml]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]
            [clojure.java.io :as io])
  (:import (java.io File)))

(defstate reader-flags-db :start (db/db-access :reader-flag conf))
(defstate dictionary-combined-db :start (db/db-access :dictionary-combined conf))

(defn list-readers []
  (db/list! reader-flags-db 100))

(defn get-default-flags []
  {:English :YES
   :German  :NO})

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

(defn list-dict-files []
  (filter #(.isFile ^File %) (file-seq (io/file (or (System/getenv "DICT_PATH") "grammar/dictionary")))))

(defn initialize []
  (doseq [f (list-dict-files)]
    (let [{:keys [phrases partOfSpeech name]} (yaml/parse-string (slurp f))
          filename (utils/get-name f)]
      (when-not (get-dictionary-item filename)
        (create-dictionary-item
          {:key          filename
           :name         (or name filename)
           :phrases      phrases
           :partOfSpeech (when (some? partOfSpeech)
                           (keyword partOfSpeech))})))))
