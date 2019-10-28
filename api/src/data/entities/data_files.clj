(ns data.entities.data-files
  (:require [clojure.data.csv :as csv]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate data-files-db :start (db/db-access :data-files))

(defn store!
  "Expected keys are :filename and :content everything else is optional"
  [data-file]
  (let [id (utils/gen-uuid)]
    (db/write! data-files-db id data-file)
    id))

(defn fetch [id offset limit]
  (when-let [{content :content file-name :filename} (db/read! data-files-db id)]
    (let [rows (csv/read-csv content)
          field-names (first rows)
          records (rest rows)]
      {:id           id
       :fileName     file-name
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

(defn listing [offset limit recordOffset recordLimit]
  (let [data-files (->> (db/list! data-files-db Integer/MAX_VALUE)
                        (drop offset)
                        (take limit))]
    {:dataFiles  (map (fn [{id :id}]
                        (fetch id recordOffset recordLimit))
                      (->> data-files (drop offset) (take limit)))
     :offset     offset
     :limit      limit
     :totalCount (count data-files)}))

(defn read-data-file-content [_ key]
  (:content (db/read! data-files-db key)))
