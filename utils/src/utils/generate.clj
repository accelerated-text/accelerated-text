(ns utils.generate
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [jsonista.core :as json]
            [org.httpkit.client :as http])
  (:import (java.io File)))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn read-data [csv-file]
  (with-open [reader (io/reader csv-file)]
    (doall (csv/read-csv reader))))

(defn- post-generate [body]
  @(http/request {:url     "http://localhost:3001/nlg/_bulk/"
                  :method  :post
                  :headers {"Content-Type" "application/json"}
                  :body    (json/write-value-as-string body)}
                 (fn [{:keys [status body]}]
                   (let [{result-ids :resultIds message :message} (json/read-value body read-mapper)]
                     (if (= status 200)
                       result-ids
                       (log/error message))))))

(defn- fetch-results [id]
  @(http/request {:url (format "http://localhost:3001/nlg/%s?format=raw" id)}
                 (fn [{:keys [status body]}]
                   (if (= status 200)
                     (let [{ready? :ready :as response} (json/read-value body read-mapper)]
                       (when-not ready?
                         (Thread/sleep 1000))
                       response)
                     (log/error "Failed to fetch result with status %d" status)))))

(defn- get-results [id]
  (->> (repeatedly #(fetch-results id))
       (filter :ready)
       (first)
       (:variants)
       (vector id)))

(defn generate-bulk
  "Generate text variants for data rows. Return collection of tuples where
  first entry is original data and the second is a collection of variants."
  [document-plan language data]
  (log/infof "Generating text for %s data items" (count data))
  (let [ids (take (count data) (repeatedly #(str (java.util.UUID/randomUUID))))
        id->data (zipmap ids data)]
    (->> {:documentPlanName document-plan
          :dataRows         id->data
          :readerFlagValues {language true}}
         (post-generate)
         (map get-results)
         (map-indexed (fn [i [id variants]]
                        (log/infof "%.2f%% ready" (float (* 100 (/ (inc i) (count ids)))))
                        [(get id->data id) variants])))))

(defn save-data-with-variants
  "Merge data rows and text variants. For each text variant for the data item
  create a new row with all the data points duplicated"
  [out-file results header]
  (let [csv-data (mapcat (fn [[data variations]]
                           (let [data-row (vec (map (fn [col] (get data col)) header))]
                             (map (fn [variant]
                                    (conj data-row variant))
                                  variations)))
                         results)]
    (when (seq results)
      (with-open [writer (io/writer out-file)]
        (csv/write-csv writer (cons (conj (vec header) "Variants") csv-data)))
      (log/infof "Saved output to `%s`" (.getAbsolutePath ^File out-file)))))

(defn data->text [document-plan data-file output-file language]
  (log/infof "Generating using '%s' document plan for '%s' language" document-plan language)
  (let [[header & rows] (read-data data-file)
        results (generate-bulk document-plan language (map #(zipmap header %) rows))]
    (save-data-with-variants output-file results header)))

(defn -main [& [document-plan data-file output-path language]]
  (if (and document-plan data-file output-path)
    (data->text document-plan (io/file data-file) (io/file output-path) (if-not (str/blank? language) language "Eng"))
    (println "Usage: pass in four parameters: name of the document plan, path to a data file, path to an output file, and language code")))
