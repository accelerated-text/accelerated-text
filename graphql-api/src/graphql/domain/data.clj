(ns graphql.domain.data
  (:require [data-access.data.core :as data])
  (:import (java.io File)))

(defn get-data-file [_ {:keys [id recordOffset recordLimit] :or {recordOffset 0 recordLimit 20}} _]
  (let [{:keys [key records field-names]} (data/read-csv-file id)]
    {:id           key
     :fileName     (.getName (File. ^String key))
     :fieldNames   field-names
     :records      (for [[row record] (->> records (interleave (range)) (partition 2) (drop recordOffset) (take recordLimit))]
                     {:id     (str key ":" row)
                      :fields (for [[column field-name value] (partition 3 (interleave (range) field-names record))]
                                {:id        (str key ":" row ":" column)
                                 :fieldName field-name
                                 :value     value})})
     :recordOffset recordOffset
     :recordLimit  recordLimit
     :recordCount  (count records)}))

(defn list-data-files [_ {:keys [offset limit] :as args :or {offset 0 limit 20}} _]
  (let [data-files (data/list-data-files "example-user")]
    {:dataFiles  (map #(get-data-file _ (-> args (select-keys [:recordOffset :recordLimit]) (assoc :id (get % :key))) _)
                      (->> data-files (drop offset) (take limit)))
     :offset     offset
     :limit      limit
     :totalCount (count data-files)}))
