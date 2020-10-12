(ns data.entities.reader-model
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [data.db :as db]
            [data.spec.reader-model :as reader-model]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate reader-model-db :start (db/db-access :reader-model conf))

(defn fetch [code]
  (db/read! reader-model-db code))

(defn update! [lang]
  (db/write! reader-model-db lang)
  (fetch (::reader-model/code lang)))

(defn list-readers
  ([] (list-readers 100))
  ([limit]
   (filter #(= (::reader-model/type %) :reader) (db/list! reader-model-db limit))))

(defn list-languages
  ([] (list-languages 100))
  ([limit]
   (filter #(= (::reader-model/type %) :language) (db/list! reader-model-db limit))))

(defstate reader-conf :start
  (->> "config/readers.edn"
       (io/resource)
       (io/file)
       (utils/read-edn)
       (mapv #(update! (assoc % ::reader-model/type :reader
                                ::reader-model/enabled? (contains? (:enabled-readers conf)
                                                                   (::reader-model/code %)))))))

(defstate language-conf :start
  (->> "config/languages.edn"
       (io/resource)
       (io/file)
       (utils/read-edn)
       (mapv #(update! (assoc % ::reader-model/type :language
                                ::reader-model/enabled? (contains? (:enabled-languages conf)
                                                                   (::reader-model/code %)))))))
