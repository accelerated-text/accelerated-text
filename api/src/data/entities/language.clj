(ns data.entities.language
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [data.db :as db]
            [data.spec.language :as lang]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate language-codes :start (->> (io/resource "config/language-codes.csv")
                                     (io/file)
                                     (utils/read-csv)
                                     (into {})
                                     (clojure.set/map-invert)))

(defn get-language-code [lang]
  (if (contains? language-codes lang)
    (get language-codes lang)
    (throw (Exception. (format "Language code not found for: `%s`" lang)))))

(defn get-language [name]
  #::lang{:code     (get-language-code name)
          :name     name
          :enabled? (contains? (:enabled-languages conf) name)})

(defstate language-db :start (db/db-access :language conf))

(defn update! [langs]
  (db/write! language-db langs))

(defn fetch [code]
  (db/read! language-db code))

(defn listing
  ([] (listing 100))
  ([limit]
   (db/list! language-db limit)))

(defn enabled-languages []
  (filter #(true? (::lang/enabled? %)) (listing)))

(defn default-languages []
  (let [{:keys [available-languages enabled-languages]} conf]
    (map get-language (set/union available-languages enabled-languages))))

(defstate language :start (update! (default-languages)))
