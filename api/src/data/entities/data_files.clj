(ns data.entities.data-files
  (:require [api.config :refer [conf]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.db :as db]
            [data.entities.data-files.format-coercion :as format-coercion]
            [data.entities.data-files.row-selection :as row-selection]
            [data.spec.data-file :as data-file]
            [data.utils :as utils]
            [dk.ative.docjure.spreadsheet :as excel]
            [mount.core :refer [defstate]]
            [data.entities.user-group :as user-group]
            [data.spec.user-group :as ug]))

(defstate data-files-db :start (db/db-access :data-files conf))

(defn build-key [filename group-id]
  (str group-id "#" filename))

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
         (format-coercion/coerce-data-types)
         (csv/write-csv *out*)
         (with-out-str))))

(defn convert-file [{:keys [filename content]}]
  (cond
    (instance? String content) content
    (and (some? filename) (str/ends-with? filename ".xlsx")) (read-xlsx content)
    :else (slurp content)))

(defn read-data-file [key group-id]
  (log/infof "Searching for data file: `%s`" key)
  (db/read! data-files-db (build-key key group-id)))

(defn delete-data-file! [key group-id]
  (when (read-data-file key group-id)
    (log/infof "Deleting data file: `%s`" key)
    (db/delete! data-files-db (build-key key group-id))))

(defn store!
  "Expected keys are :filename and :content everything else is optional"
  [{filename :filename :as data-file} group-id]
  (let [key (build-key filename group-id)]
    (delete-data-file! filename group-id)
    (log/infof "Storing data file: `%s`" filename)
    (db/write! data-files-db key
               #::data-file{:name      filename
                            :timestamp (utils/ts-now)
                            :content   (convert-file data-file)})
    (user-group/link-file group-id key))
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

(defn fetch-most-relevant [id offset limit group-id]
  (let [{:keys [filename header rows total]} (some-> (read-data-file id group-id) (parse-data))
        sampled-rows (row-selection/sample rows (:relevant-items-limit conf))
        m (row-selection/distance-matrix sampled-rows)
        selected-rows (drop offset (row-selection/select-rows m sampled-rows limit))]
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

(defn fetch [id offset limit group-id]
  (some-> (read-data-file id group-id)
          (read-content offset limit)))

(defn swap-data-id [{:keys [fileName] :as data}]
  (assoc data :id fileName))

(defn listing
  ([group-id] (listing group-id 0 Integer/MAX_VALUE 0 Integer/MAX_VALUE))
  ([group-id offset limit recordOffset recordLimit]
   (let [data-files (-> (user-group/get-or-create-group group-id) ::ug/data-files)]
     {:dataFiles  (->> data-files
                       (drop offset)
                       (take limit)
                       (map #(read-content % recordOffset recordLimit))
                       (map swap-data-id))
      :offset     offset
      :limit      limit
      :totalCount (count data-files)})))

(defn data-file-path []
  (or (System/getenv "DATA_FILES") "resources/data-files"))

(defstate data-files
  :start (doseq [f (utils/list-files (data-file-path) #{".csv" ".xlsx"})]
           (store! {:filename (.getName f)
                    :content  f}
                   user-group/DUMMY-USER-GROUP-ID))
  :stop (doseq [{id :id} (:dataFiles (listing user-group/DUMMY-USER-GROUP-ID))]
          (delete-data-file! id user-group/DUMMY-USER-GROUP-ID)))
