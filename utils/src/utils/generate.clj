(ns utils.generate
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [org.httpkit.client :as http]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [clojure.string :as string]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn read-data [csv-file]
  (with-open [reader (io/reader csv-file)]
    (let [csv-data (doall (csv/read-csv reader))]
      (map zipmap
           (repeat (first csv-data))
           (rest csv-data)))))

(defn- post-generate [body]
  (-> @(http/post "http://localhost:3001/nlg/_bulk/"
                  {:headers {"Content-Type" "application/json"}
                   :body    (json/write-value-as-string body)})
      :body (json/read-value read-mapper) :resultIds))

(defn- get-results [id]
  [id (-> (format "http://localhost:3001/nlg/%s?format=raw" id)
          http/get deref
          :body (json/read-value read-mapper)
          :variants)])

(defn generate-bulk
  "Generate text variants for data rows. Return collection of tuples where
  first entry is original data and the second is a collection of variants."
  [document-plan data]
  (let [ids      (take (count data) (repeatedly #(str (java.util.UUID/randomUUID))))
        id->data (zipmap ids data)]
    (->> {:documentPlanName document-plan
          :dataRows         id->data
          :readerFlagValues {"English" true}}
         (post-generate)
         (map get-results)
         (map (fn [[id variants]]
                [(get id->data id) variants])))))

(defn save-data-with-variants
  "Merge data rows and text variantas. For each text variant for the data item
  create a new row with all the data points duplicated"
  [out-file results]
  (let [header (vec (map name (-> results first first keys)))
        csv-data (mapcat (fn [[data variations]]
                        (let [data-row (vec (map (fn [col] (get data col)) header))]
                          (map (fn [variant]
                                 (conj data-row variant))
                               variations)))
                      results)]
    (with-open [writer (io/writer out-file)]
      (csv/write-csv writer
                     (cons (conj (vec (map name (-> results first first keys))) "Variants")
                           csv-data)))))

(defn data->text [document-plan data-file output-file]
  (->> data-file
       (read-data)
       (generate-bulk document-plan)
       (save-data-with-variants output-file)))
