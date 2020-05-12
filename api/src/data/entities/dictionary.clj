(ns data.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]])
  (:import (java.io PushbackReader)))

(defstate reader-flags-db :start (db/db-access :reader-flag conf))

(defstate dictionary-db :start (db/db-access :dictionary-multilang conf))

(def language-config (->> "config/language-codes.csv"
                          (io/resource)
                          (slurp)
                          (string/split-lines)
                          (reduce (fn [m line]
                                    (let [[code name] (string/split line #",")]
                                      (assoc m name (string/capitalize code))))
                                  {})))

(defn default-language-flag []
  (or (System/getenv "DEFAULT_LANGUAGE") "English"))

(defn get-default-flags []
  (-> (:enabled-languages conf)
      (zipmap (repeat "NO"))
      (assoc (default-language-flag) "YES")))

(defn list-readers []
  (get-default-flags))

(defn flag->lang [flag] (get language-config flag))

(defn default-language [] (flag->lang (default-language-flag)))

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

(defn dictionary-path []
  (or (System/getenv "DICT_PATH") "resources/dictionary"))

(defn initialize []
  (doseq [[flag value] (get-default-flags)]
    (db/write! reader-flags-db flag value))
  (doseq [f (utils/list-files (dictionary-path))]
    (with-open [r (io/reader f)]
      (doseq [item (edn/read (PushbackReader. r))]
        (create-dictionary-item (utils/add-ns-to-map "acc-text.nlg.dictionary.item" item))))))
