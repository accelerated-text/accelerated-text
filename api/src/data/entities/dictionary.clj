(ns data.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]])
  (:import (java.io File PushbackReader)))

(defstate reader-flags-db :start (db/db-access :reader-flag conf))

(defstate dictionary-combined-db :start (db/db-access :dictionary-combined conf))

(defstate dictionary-multilang-db :start (db/db-access :dictionary-multilang conf))

(def languages ["English" "Estonian" "German" "Latvian" "Russian"])

(defn list-readers []
  (db/list! reader-flags-db 100))

(defn default-language-flag []
  (or (System/getenv "DEFAULT_LANGUAGE") "English"))

(defn get-default-flags []
  (-> languages
      (zipmap (repeat "NO"))
      (assoc (default-language-flag) "YES")))

(defn flag->lang [f]
  (case f
    "English" "Eng"
    "Estonian" "Est"
    "German" "Ger"
    "Latvian" "Lat"
    "Russian" "Rus"))

(defn default-language []
  (flag->lang (default-language-flag)))

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
    :flags (assoc default-flags (default-language-flag) default-usage)}))

(defn create-dictionary-item [{:keys [key name phrases partOfSpeech]}]
  (when-not (str/blank? name)
    (db/write! dictionary-combined-db key {:name         name
                                           :partOfSpeech partOfSpeech
                                           :phrases      (map #(text->phrase % key :YES) phrases)})))

(defn delete-dictionary-item [key]
  (db/delete! dictionary-combined-db key))

(defn update-dictionary-item [item]
  (db/update! dictionary-combined-db (:key item) (dissoc item :key)))

(defn write-dictionary-item [{id ::dictionary-item/id :as item}]
  (db/write! dictionary-multilang-db (or id (utils/gen-uuid)) item))

(defn scan-dictionary
  ([keys]
   (db/scan! dictionary-multilang-db {:keys keys}))
  ([keys languages]
   (db/scan! dictionary-multilang-db {:keys keys :languages languages})))

(defn list-dictionary-items
  ([] (list-dictionary-items 100))
  ([limit]
   (db/list! dictionary-multilang-db limit)))

(defn list-dictionary-files []
  (->> (file-seq (io/file (or (System/getenv "DICT_PATH") "grammar/dictionary")))
       (filter #(.isFile ^File %))
       (filter #(str/ends-with? (.getName %) "edn"))))

(defn initialize []
  (doseq [[flag value] (get-default-flags)]
    (db/write! reader-flags-db flag value))
  (doseq [f (list-dictionary-files)]
    (with-open [r (io/reader f)]
      (doseq [item (edn/read (PushbackReader. r))]
        (write-dictionary-item item)))))
