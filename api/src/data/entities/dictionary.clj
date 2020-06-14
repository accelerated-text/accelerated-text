(ns data.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]])
  (:import (java.io PushbackReader)))

(defstate dictionary-db :start (db/db-access :dictionary conf))

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

(defn scan-dictionary
  ([keys]
   (db/scan! dictionary-db {:keys keys}))
  ([keys languages]
   (db/scan! dictionary-db {:keys keys :languages languages}))
  ([keys languages categories]
   (db/scan! dictionary-db {:keys keys :languages languages :categories categories})))

(defn find-dictionary-item [{::dictionary-item/keys [key language category]}]
  (first (scan-dictionary [key] [language] [category])))

(defn create-dictionary-item [{id ::dictionary-item/id :as item}]
  (let [item-id (or id (::dictionary-item/id (find-dictionary-item item)) (utils/gen-uuid))]
    (when (some? (get-dictionary-item item-id))
      (delete-dictionary-item item-id))
    (db/write! dictionary-db item-id item)
    (get-dictionary-item item-id)))

(defn build-dictionaries [keys language-codes]
  (group-by ::dictionary-item/language (scan-dictionary keys language-codes)))

(defn dictionary-path []
  (or (System/getenv "DICT_PATH") "resources/dictionary"))

(defn add-dictionary-items-from-file [f]
  (when (= ".edn" (utils/get-ext f))
    (with-open [r (io/reader f)]
      (mapv #(->> % (utils/add-ns-to-map "acc-text.nlg.dictionary.item") (create-dictionary-item))
            (edn/read (PushbackReader. r))))))

(defn initialize []
  (doseq [{id ::dictionary-item/id}
          (set/difference
            (set (list-dictionary-items 9999))
            (set (mapcat add-dictionary-items-from-file (utils/list-files (dictionary-path)))))]
    (delete-dictionary-item id)))
