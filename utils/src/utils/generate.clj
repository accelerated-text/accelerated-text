(ns utils.generate
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [org.httpkit.client :as http]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn read-data [csv-file]
  (with-open [reader (io/reader csv-file)]
    (let [csv-data (doall (csv/read-csv reader))]
      (map zipmap
           (repeat (first csv-data))
           (rest csv-data)))))

(defn- post-generate [body]
  (let [{:keys [status body]}
        @(http/post "http://localhost:3001/nlg/_bulk/"
                    {:headers {"Content-Type" "application/json"}
                     :body    (json/write-value-as-string body)})
        body (json/read-value body read-mapper)]
    (if (= status 200)
      (:resultIds body)
      (log/error (:message body)))))

(defn- get-results [id]
  (->> (repeatedly #(let [{ready? :ready :as response}
                          (-> (format "http://localhost:3001/nlg/%s?format=raw" id)
                              http/get deref
                              :body (json/read-value read-mapper))]
                      (when-not ready?
                        (Thread/sleep 1000))
                      response))
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
                        (log/info "%d out of %s instances ready" i (count ids))
                        [(get id->data id) variants])))))

(defn save-data-with-variants
  "Merge data rows and text variants. For each text variant for the data item
  create a new row with all the data points duplicated"
  [out-file results]
  (log/infof "Total %s results to save" (count results))
  (let [header (vec (map name (-> results first first keys)))
        csv-data (mapcat (fn [[data variations]]
                           (log/infof "Data %s has %s variations" (get data (first header)) (count variations))
                           (let [data-row (vec (map (fn [col] (get data col)) header))]
                             (map (fn [variant]
                                    (conj data-row variant))
                                  variations)))
                         results)]
    (when (seq results)
      (with-open [writer (io/writer out-file)]
        (csv/write-csv writer
                       (cons (conj (vec (map name (-> results first first keys))) "Variants")
                             csv-data))))))

(defn data->text [document-plan data-file output-file language]
  (log/infof "Generating using '%s' document plan for '%s' language" document-plan language)
  (->> data-file
       (read-data)
       (generate-bulk document-plan language)
       (save-data-with-variants output-file)))

(defn -main [& [document-plan data-file output-file language]]
  (if (and document-plan data-file output-file)
    (data->text document-plan data-file output-file (if-not (str/blank? language) language "Eng"))
    (println "Usage: pass in four parameters: name of the document plan, path to a data file, path to an output file, and language code")))
