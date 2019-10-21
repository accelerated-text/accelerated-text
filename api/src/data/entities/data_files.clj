(ns data.entities.data-files
  (:require [clojure.data.csv :as csv]
            [data.db.dynamo-ops :as ops]
            [data.utils :as utils]))

(defn store! [data-file]
  (let [id (utils/gen-uuid)]
    (ops/write! (ops/db-access :data-files) id data-file)
    id))

(defn fetch [id offset limit]
  (when-let [{contents :contents file-name :fileName} (ops/read! (ops/db-access :data-files) id)]
    (let [rows (csv/read-csv contents)
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
  (let [data-files (->> (ops/list! (ops/db-access :data-files) Integer/MAX_VALUE)
                        (drop offset)
                        (take limit))]
    {:dataFiles  (map (fn [{id :id}]
                        (fetch id recordOffset recordLimit))
                      (->> data-files (drop offset) (take limit)))
     :offset     offset
     :limit      limit
     :totalCount (count data-files)}))
