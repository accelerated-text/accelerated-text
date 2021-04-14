(ns data.entities.data-files
  (:require [api.config :refer [conf]]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [data.db :as db]
            [data.utils :as utils]
            [data.entities.data-files.row-selection :as row-selection]
            [data.spec.data-file :as data-file]
            [dk.ative.docjure.spreadsheet :as excel]
            [mount.core :refer [defstate]]))

(defstate data-files-db :start (db/db-access :data-files conf))

(defn transpose [m] (apply mapv vector m))

(defn coerce-ints [coll]
  (if (every? #(and (str/ends-with? (str %) ".0")
                    (number? %)) coll)
    (map int coll)
    coll))

(defn coerce-data-types
  "When reading Excel files ints are converted to floats.
  There might be other similar data type mismatches.
  Detect such cases here and convert accordingly.
  Expecting `rows` to have a header row"
  [rows]
  (->> rows
       (transpose)
       (map (fn [[head & items]] (cons head (coerce-ints items))))
       (transpose)))

(defn read-xlsx [content]
  (with-open [is (io/input-stream content)]
    (->> is
         (excel/load-workbook-from-stream)
         (excel/sheet-seq)
         (first)
         (excel/row-seq)
         (remove nil?)
         (map excel/cell-seq)
         (map #(map excel/read-cell %))
         (remove #(every? nil? %))
         (coerce-data-types)
         (csv/write-csv *out*)
         (with-out-str))))

(defn convert-file [{:keys [filename content]}]
  (cond
    (instance? String content) content
    (str/ends-with? filename ".xlsx") (read-xlsx content)
    :else (slurp content)))

(defn read-data-file [key]
  (log/infof "Searching for data file: `%s`" key)
  (db/read! data-files-db key))

(defn delete-data-file! [key]
  (log/infof "Deleting data file: `%s`" key)
  (db/delete! data-files-db key))

(defn store!
  "Expected keys are :filename and :content everything else is optional"
  [{filename :filename :as data-file}]
  (when (some? (read-data-file filename))
    (delete-data-file! filename))
  (log/infof "Storing data file: `%s`" filename)
  (db/write! data-files-db filename
             #::data-file{:name      filename
                          :timestamp (utils/ts-now)
                          :content   (convert-file data-file)})
  filename)

(defn parse-data
  ([data-file]
   (parse-data data-file 0 Integer/MAX_VALUE))
  ([{::data-file/keys [id name content]} offset limit]
   (let [[header & rows] (map #(map str/trim %) (cond-> content (some? content) (csv/read-csv)))]
     {:id       id
      :filename name
      :header   (vec header)
      :rows     (take limit (drop offset rows))
      :offset   offset
      :limit    limit
      :total    (count rows)})))

(defn read-content [data offset limit]
  (let [{:keys [id filename header rows total]} (parse-data data offset limit)]
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

(defn fetch-most-relevant [id offset limit]
  (let [{:keys [filename header rows total]} (some-> id (read-data-file) (parse-data))
        sampled-rows                         (row-selection/sample rows (:relevant-items-limit conf))
        m                                    (row-selection/distance-matrix sampled-rows)
        selected-rows                        (drop offset (row-selection/select-rows m sampled-rows limit))]
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

(defn fetch [id offset limit]
  (some-> id
          (read-data-file)
          (read-content offset limit)))

(defn listing
  ([] (listing 0 Integer/MAX_VALUE 0 Integer/MAX_VALUE))
  ([offset limit recordOffset recordLimit]
   (let [data-files (db/list! data-files-db Integer/MAX_VALUE)]
     {:dataFiles  (->> data-files
                       (drop offset)
                       (take limit)
                       (map #(read-content % recordOffset recordLimit)))
      :offset     offset
      :limit      limit
      :totalCount (count data-files)})))

(defn data-file-path []
  (or (System/getenv "DATA_FILES") "resources/data-files"))

(defstate data-files
  :start (doseq [f (utils/list-files (data-file-path) #{".csv" ".xlsx"})]
           (store! {:filename (.getName f)
                    :content  f}))
  :stop (doseq [{id :id} (:dataFiles (listing))]
          (delete-data-file! id)))
