(ns graphql.domain.data
  (:require [clojure.set :as set]
            [data-access.data.core :as data]))

(defn list-data-files [_ {:keys [offset limit] :or {offset 0 limit 20}} _]
  (let [data-files (->> (data/list-data-files "example-user")
                        (map #(select-keys % [:key :file-name :field-names]))
                        (map #(set/rename-keys % {:key :id :file-name :fileName :field-names :fieldNames})))]
    {:dataFiles  (->> data-files (drop offset) (take limit))
     :offset     offset
     :limit      limit
     :totalCount (count data-files)}))

(defn get-data-file [_ {:keys [id recordOffset recordLimit] :or {recordOffset 0 recordLimit 20}} _]
  (let [{:keys [key records field-names]} (data/read-csv-file id)]
    {:id           key
     :fieldNames   field-names
     :records      (for [[row record] (->> records (interleave (range)) (partition 2) (drop recordOffset) (take recordLimit))]
                     {:id     (str key ":" (inc row))
                      :fields (for [[column field-name value] (partition 3 (interleave (range) field-names record))]
                                {:id        (str key ":" (inc row) ":" (inc column))
                                 :fieldName field-name
                                 :value     value})})
     :recordOffset recordOffset
     :recordLimit  recordLimit
     :recordCount  (count records)}))
