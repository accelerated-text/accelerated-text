(ns data.entities.reader-model
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [data.db :as db]
            [data.spec.reader-model :as reader-model]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate reader-model-db :start (db/db-access :reader-model conf))

(defn fetch [code]
  (db/read! reader-model-db code))

(defn update! [reader]
  (db/write! reader-model-db reader)
  (fetch (::reader-model/code reader)))

(defn delete! [reader]
  (db/delete! reader-model-db (::reader-model/code reader)))

(defn list-reader-model
  ([] (list-reader-model 100))
  ([limit]
   (db/list! reader-model-db limit)))

(defn available-readers []
  (filter (fn [{::reader-model/keys [type available?]}]
            (and (= type :reader) (true? available?)))
          (list-reader-model)))

(defn available-languages []
  (filter (fn [{::reader-model/keys [type available?]}]
            (and (= type :language) (true? available?)))
          (list-reader-model)))

(defn available-reader-model []
  (filter #(true? (::reader-model/available? %)) (list-reader-model)))

(defn reader-model-config-path []
  (io/file (get conf :config-path (io/resource "config")) "readers.edn"))

(defn language-config-path []
  (io/file (get conf :config-path (io/resource "config")) "languages.edn"))

(defstate reader-conf :start
  (do
    (doseq [reader (available-readers)] (delete! reader))
    (->> (reader-model-config-path)
         (utils/read-edn)
         (filter ::reader-model/available?)
         (mapv #(update! (assoc % ::reader-model/type :reader
                                  ::reader-model/enabled? (contains? (:enabled-readers conf)
                                                                     (str/capitalize (::reader-model/code %)))))))))

(defstate language-conf :start
  (do
    (doseq [lang (available-languages)] (delete! lang))
    (->> (language-config-path)
         (utils/read-edn)
         (filter ::reader-model/available?)
         (mapv #(update! (assoc % ::reader-model/type :language
                                  ::reader-model/enabled? (contains? (:enabled-languages conf)
                                                                     (str/capitalize (::reader-model/code %)))))))))
