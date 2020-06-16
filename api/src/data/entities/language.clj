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
    (throw (Exception. (format "Unknown language code: `%s`" code)))))

(defn get-language
  ([code] (get-language code (contains? (:enabled-languages conf) code)))
  ([code enabled?]
   #::lang{:code     code
           :name     (get-language-name code)
           :enabled? enabled?}))

(defstate language-db :start (db/db-access :language conf))

(defn fetch [code]
  (db/read! language-db code))

(defn update! [lang]
  (db/write! language-db lang)
  (fetch (::lang/code lang)))

(defn list-languages
  ([] (list-languages 100))
  ([limit]
   (db/list! language-db limit)))

(defstate language :start (doseq [code (set/union (:available-languages conf) (:enabled-languages conf))]
                            (update! (get-language code))))
