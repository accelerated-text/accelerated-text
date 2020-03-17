(ns data.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [data.db :as db]
            [data.utils :as utils]
            [clojure.tools.logging :as log]
            [mount.core :refer [defstate]])
  (:import (java.io PushbackReader)))

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

(defn list-dictionary-items
  ([] (list-dictionary-items 100))
  ([limit]
   (db/list! dictionary-db limit)))

(defn get-dictionary-item [id]
  (db/read! dictionary-db id))

(defn delete-dictionary-item [id]
  (db/delete! dictionary-db id))

(defn update-dictionary-item [{id ::dictionary-item/id :as item}]
  (db/update! dictionary-db id item))

(defn create-dictionary-item [{id ::dictionary-item/id :as item}]
  (log/tracef "Creating: %s" item)
  (let [item-id (or id (utils/gen-uuid))]
    (db/write! dictionary-db item-id item)
    (get-dictionary-item item-id)))

(defn scan-dictionary
  ([keys]
   (db/scan! dictionary-db {:keys keys}))
  ([keys languages]
   (db/scan! dictionary-db {:keys keys :languages languages})))

(defn dictionary-path []
  (or (System/getenv "DICT_PATH") "resources/dictionary"))

(defn initialize []
  (doseq [[flag value] (get-default-flags)]
    (db/write! reader-flags-db flag value))
  (doseq [f (utils/list-files (dictionary-path))]
    (with-open [r (io/reader f)]
      (doseq [item (edn/read (PushbackReader. r))]
        (create-dictionary-item item)))))
