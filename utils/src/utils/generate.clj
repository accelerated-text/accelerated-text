(ns utils.generate
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn read-data [csv-file]
  (with-open [reader (io/reader csv-file)]
    (let [csv-data (doall (csv/read-csv reader))]
      (map zipmap
           (repeat (first csv-data))
           (rest csv-data)))))

(def colog [{"Department Brand" "Acqua di Parma" "Division" "toiletries" "Subclass" "shaving cream" "Color (UDA)" "" "Gender" "man" "Class" "cologne"}])

(defn write-variants-to-file [variants id-column output-dir]
  (prn "Writing: " (get variants id-column))
  (-> output-dir (io/file) .mkDirs)
  (doseq [line variants]
    (spit (format "%s/%s.txt" output-dir (get variants id-column))
          (str line "\n")
          :append true)))

(defn generate-bulk [document-plan-name data]
  (let [ids (take (count data) (repeatedly #(str (java.util.UUID/randomUUID))))]
    (api.nlg.service/generate-request-bulk
     {:documentPlanName document-plan-name
      :dataRows         (zipmap ids data)
      :readerFlagValues {"English" true}})
    (map (fn [id data-row]
           (let [{{variants :variants} :body} (api.nlg.service/get-result
                                               {:parameters {:path  {:id (str id)}
                                                             :query {:format "raw"}}})]
             (assoc data-row "results" (clojure.string/join "\n\n" variants))))
         ids data)))

(defn save-results-to-dir [data-file output-dir document-plan id-column]
  (->> data-file
      (read-data)
      (generate-bulk document-plan)
      (write-variants-to-file id-column output-dir)))
