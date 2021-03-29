(ns api.test-utils
  (:require [api.server :as server]
            [api.utils :as utils]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dictionary]
            [data.entities.document-plan :as dp]
            [jsonista.core :as json])
  (:import (org.httpkit BytesInputStream)))

(def headers {"origin"                         "http://localhost:8080"
              "host"                           "0.0.0.0:3001"
              "user-agent"                     "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0"
              "access-control-request-headers" "content-type"
              "referer"                        "http://localhost:8080/"
              "connection"                     "keep-alive"
              "accept"                         "*/*"
              "accept-language"                "en-US,en;q=0.5"
              "content-type"                   "application/json"
              "access-control-request-method"  "POST"
              "accept-encoding"                "gzip, deflate"
              "dnt"                            "1"})

(defn encode [body]
  (let [content (json/write-value-as-string body)]
    (BytesInputStream. (.getBytes content) (count content))))

(defn log-failure [{:keys [status body] :as resp}]
  (when-not (contains? #{200 404} status)
    (log/errorf "Error: %s" body))
  resp)

(defn q
  ([uri method body]
   (q uri method body {}))
  ([uri method body query]
   (log/debugf "Doing request %s to URL: %s Body: %s" method uri body)
   (log/spyf :debug "Response: %s"
             (-> {:uri uri :request-method method :body body :query-params query}
                 (assoc :headers headers)
                 (update :body encode)
                 (server/app)
                 (update :body #(json/read-value % utils/read-mapper))
                 (log-failure)))))

(defn load-data-file [filename]
  (data-files/store!
    {:filename filename
     :content  (io/file (format "test/resources/data-files/%s" filename))}
   0))

(defn load-document-plan [filename]
  (log/infof "Loading test document plan `%s`" filename)
  (let [{id :id :as dp} (dp/load-document-plan (io/file (format "test/resources/document-plans/%s.json" filename)))]
    (dp/add-document-plan dp id)
    id))

(defn load-dictionary [filename]
  (log/infof "Loading test dictionary `%s`" filename)
  (doseq [dict-item (dictionary/read-dictionary-items-from-file (io/file (format "test/resources/dictionary/%s.edn" filename)))]
    (dictionary/create-dictionary-item dict-item)))

(defn get-result [result-id]
  (when (some? result-id)
    (letfn [(request! [] (q (str "/nlg/" result-id) :get nil {:format "raw"}))]
      (loop [retry-count 0 {{:keys [ready variants]} :body} (request!)]
        (cond
          (true? ready) variants
          (< 10 retry-count) (throw (Exception. "Result was not ready after 10 seconds."))
          :else (do (Thread/sleep 1000) (recur (inc retry-count) (request!))))))))

(defn generate-text [{:keys [document-plan-name data-file-name reader-flags async]}]
  (let [{:keys [status body]} (q "/nlg/" :post (cond-> {:documentPlanId   (load-document-plan document-plan-name)
                                                        :readerFlagValues (or reader-flags {"Eng" true})
                                                        :dataId           (if (some? data-file-name)
                                                                            (load-data-file data-file-name)
                                                                            "")}
                                                       (false? async) (assoc :async false)))]
    (when (= 200 status)
      (if (false? async) body (get-result (:resultId body))))))

(defn generate-text-bulk [{:keys [document-plan-name data-rows reader-flags]}]
  (let [{:keys [status body]} (q "/nlg/_bulk/" :post {:documentPlanId   (load-document-plan document-plan-name)
                                                      :readerFlagValues (or reader-flags {"Eng" true})
                                                      :dataRows         data-rows})]
    (when (= 200 status)
      (reduce (fn [m result-id]
                (assoc m result-id (get-result result-id)))
              {}
              (:resultIds body)))))
