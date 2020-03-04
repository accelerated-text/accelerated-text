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

(defstate dictionary-db :start (db/db-access :dictionary-multilang conf))

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

(defn list-items
  ([] (list-items 100))
  ([limit]
   (db/list! dictionary-db limit)))

(defn get-item [id]
  (db/read! dictionary-db id))

(defn delete-item [id]
  (db/delete! dictionary-db id))

(defn update-item [{id ::dictionary-item/id :as item}]
  (db/update! dictionary-db id item))

(defn write-item [{id ::dictionary-item/id :as item}]
  (let [item-id (or id (utils/gen-uuid))]
    (db/write! dictionary-db item-id item)
    (get-item item-id)))

(defn scan
  ([keys]
   (db/scan! dictionary-db {:keys keys}))
  ([keys languages]
   (db/scan! dictionary-db {:keys keys :languages languages})))

(defn list-files []
  (->> (file-seq (io/file (or (System/getenv "DICT_PATH") "grammar/dictionary")))
       (filter #(.isFile ^File %))
       (filter #(str/ends-with? (.getName %) "edn"))))

(defn initialize []
  (doseq [[flag value] (get-default-flags)]
    (db/write! reader-flags-db flag value))
  (doseq [f (list-files)]
    (with-open [r (io/reader f)]
      (doseq [item (edn/read (PushbackReader. r))]
        (write-item item)))))
