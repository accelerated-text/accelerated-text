(ns api.graphql.domain.data
  (:require [clojure.data.csv :as csv]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [data.db.config :as config]
            [data.db.s3 :as s3])
  (:import (java.io File)))

(defn- read-data-file-from-s3 [id offset limit]
  (when-let [raw-csv-string (s3/read-file config/data-bucket id)]
    (let [rows (csv/read-csv raw-csv-string)
          field-names (first rows)
          records (rest rows)]
      {:id           id
       :fileName     (.getName (File. ^String id))
       :fieldNames   field-names
       :records      (for [[row record] (->> (map vector (range) records) (drop offset) (take limit))]
                       {:id     (str id ":" row)
                        :fields (for [[column field-name value] (map vector (range) field-names record)]
                                  {:id        (str id ":" row ":" column)
                                   :fieldName field-name
                                   :value     value})})
       :recordOffset offset
       :recordLimit  limit
       :recordCount  (count records)})))

(defn get-data-file [_ {:keys [id recordOffset recordLimit] :or {recordOffset 0 recordLimit 20}} _]
  (if-let [data-file (read-data-file-from-s3 id recordOffset recordLimit)]
    (resolve-as data-file)
    (resolve-as nil {:message (format "Cannot find data file with id `%s`." id)})))

(defn list-data-files [_ {:keys [offset limit recordOffset recordLimit] :or {offset 0 limit 20 recordOffset 0 recordLimit 20}} _]
  (let [data-files (s3/list-objects config/data-bucket "example-user")]
    (resolve-as
      {:dataFiles  (map (fn [{id :key}]
                          (read-data-file-from-s3 id recordOffset recordLimit))
                        (->> data-files (drop offset) (take limit)))
       :offset     offset
       :limit      limit
       :totalCount (count data-files)})))
