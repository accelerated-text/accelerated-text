(ns data-access.data.core
  (:require [data-access.db.config :as config]
            [data-access.db.s3 :as s3]
            [data-access.utils :as utils]
            [clojure.data.csv :as csv]))

(defn read-csv-file [s3-key]
  (let [raw-csv-string (s3/read-file config/data-bucket s3-key)
        rows (csv/read-csv raw-csv-string)]
    {:records     (rest rows)
     :field-names (first rows)
     :key         s3-key}))

(defn list-data-files [user]
  (map #(assoc % :field-names (->> (get % :key) (s3/read-file config/data-bucket) (csv/read-csv) (first)))
       (s3/list-objects config/data-bucket user)))
