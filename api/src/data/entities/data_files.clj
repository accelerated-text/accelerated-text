(ns data.entities.data-files
  (:require [api.config :refer [conf]]
            [api.nlg.enrich.data :as data-enrich]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate data-files-db :start (db/db-access :data-files conf))

(defn store!
  "Expected keys are :filename and :content everything else is optional"
  [data-file]
  (let [id (utils/gen-uuid)]
    (log/infof "Storing `%s` with id: `%s`" (:filename data-file) id)
    (db/write! data-files-db id data-file)
    id))

(defn read-data-file [key]
  (db/read! data-files-db key))

(defn parse-data [{:keys [filename content]}]
  (when (some? content)
    (let [[header & rows] (->> content (csv/read-csv) (map #(map str/trim %)))]
      {:filename filename
       :header   header
       :rows     rows})))

(defn fetch [id offset limit]
  (when-let [{:keys [filename header rows]} (parse-data (read-data-file id))]
    (cond-> {:id           id
             :fileName     filename
             :fieldNames   (vec header)
             :records      (for [[row record] (->> (map vector (range) rows) (drop offset) (take limit))]
                             {:id     (str id ":" row)
                              :fields (for [[column field-name value] (map vector (range) header record)]
                                        {:id        (str id ":" row ":" column)
                                         :fieldName field-name
                                         :value     value})})
             :recordOffset offset
             :recordLimit  limit
             :recordCount  (count rows)}
            (data-enrich/enable-enrich?) (data-enrich/enrich))))

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
