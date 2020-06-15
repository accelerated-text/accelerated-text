(ns data.entities.language
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [data.db :as db]
            [data.spec.language :as lang]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate language-names :start (->> "config/language-names.csv" (io/resource) (io/file) (utils/read-csv) (into {})))

(defn get-language-name [code]
  (if (contains? language-names code)
    (get language-names code)
    (throw (Exception. (format "Language name not found for: `%s`" code)))))

(defn get-language [code]
  #::lang{:code     code
          :name     (get-language-name code)
          :enabled? (contains? (:enabled-languages conf) code)})

(defstate language-db :start (db/db-access :language conf))

(defn update! [lang]
  (db/write! language-db lang))

(defn fetch [code]
  (db/read! language-db code))

(defn listing
  ([] (listing 100))
  ([limit]
   (db/list! language-db limit)))

(defn enabled-languages []
  (filter #(true? (::lang/enabled? %)) (listing)))

(defstate language :start (update! (let [{:keys [available-languages enabled-languages]} conf]
                                     (map get-language (set/union available-languages enabled-languages)))))
