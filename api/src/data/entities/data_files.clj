
(ns data.entities.data-files
  (:require [api.config :refer [conf]]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [data.db :as db]
            [data.utils :as utils]
            [data.entities.data-files.row-selection :as row-selection]
            [mount.core :refer [defstate]]
            [dk.ative.docjure.spreadsheet :as excel]))

(defstate data-files-db :start (db/db-access :data-files conf))


(defn binary->csv [data-file]
  (assoc data-file :content (slurp (:content data-file))))


(defn xlsx->csv [data-file]
  (let [content (:content data-file)]
    (assoc data-file :content (->> (io/input-stream content)
                                   (excel/load-workbook-from-stream)
                                   (excel/sheet-seq)
                                   (first)
                                   (excel/row-seq)
                                   (remove nil?)
                                   (map excel/cell-seq)
                                   (map #(map excel/read-cell %))
                                   (remove #(every? nil? %))
                                   (map #(str/join "," %))
                                   (str/join "\n")))))


(defn convert-file [data-file]
  (let [file-name (:filename data-file)
        format    (cond
                   (str/ends-with? file-name ".csv") :csv
                   (str/ends-with? file-name ".xlsx") :xlsx
                   :else                          :unknown)]
    (case format
          :csv     (binary->csv data-file)
          :xlsx    (xlsx->csv data-file)
          :unknown data-file)))

(defn store!
  "Expected keys are :filename and :content everything else is optional"
  [data-file]
  (let [id (utils/gen-uuid)]
    (log/infof "Storing `%s` with id: `%s`" (:filename data-file) id)
    (db/write! data-files-db id (convert-file data-file))
    id))

(defn read-data-file [key]
  (db/read! data-files-db key))

(defn parse-data
  ([data] (parse-data data 0 Integer/MAX_VALUE))
  ([data offset limit]
   (when (some? data)
     (let [[header & rows] (->> (get data :content) (csv/read-csv) (map #(map str/trim %)))]
       {:filename (get data :filename)
        :header   (vec header)
        :rows     (take limit (drop offset rows))
        :offset   offset
        :limit    limit
        :total    (count rows)}))))

(defn fetch [id offset limit]
  (when-let [{:keys [filename header rows total]} (some-> id (read-data-file) (parse-data offset limit))]
    {:id           id
     :fileName     filename
     :fieldNames   header
     :records      (map (fn [row record]
                          {:id     (str id ":" row)
                           :fields (map (fn [column field-name value]
                                          {:id        (str id ":" row ":" column)
                                           :fieldName field-name
                                           :value     value})
                                        (range)
                                        header
                                        record)})
                        (range offset (+ offset limit))
                        rows)
     :recordOffset offset
     :recordLimit  limit
     :recordCount  total}))

(defn fetch-most-relevant [id _ limit]
  (let [{:keys [filename header rows total]} (some-> id (read-data-file) (parse-data))
        sampled-rows                         (row-selection/sample rows (:relevant-items-limit conf))
        m                                    (row-selection/distance-matrix sampled-rows)
        selected-rows                        (row-selection/select-rows m sampled-rows limit)]
    {:id           id
     :fileName     filename
     :fieldNames   header
     :records      (map (fn [row record]
                          {:id     (str id ":" row)
                           :fields (map (fn [column field-name value]
                                          {:id        (str id ":" row ":" column)
                                           :fieldName field-name
                                           :value     value})
                                        (range)
                                        header
                                        record)})
                        (range 0 limit)
                        selected-rows)
     :recordOffset 0
     :recordLimit  limit
     :recordCount  total}))

(defn listing [offset limit recordOffset recordLimit]
  (let [data-files (db/list! data-files-db Integer/MAX_VALUE)]
    {:dataFiles  (map (fn [{id :id}]
                        (fetch id recordOffset recordLimit))
                      (->> data-files (drop offset) (take limit)))
     :offset     offset
     :limit      limit
     :totalCount (count data-files)}))

(defn data-file-path []
  (or (System/getenv "DATA_FILES") "resources/data-files"))

(defn initialize []
  (doseq [f (utils/list-files (data-file-path))]
    (store! {:filename (utils/get-name f)
             :content  (slurp f)})))
