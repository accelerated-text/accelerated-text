(ns nlg.utils
  (:require [cheshire.core :as cheshire]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [clojure.data.csv :as csv])
  (:import (java.io InputStream)
           (java.util UUID)))

(defn gen-uuid [] (.toString (UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))

(defn stack-trace [e]
  (reduce #(str %1 "\n" %2) (.getStackTrace ^Exception e)))

(defn add-body [resp body]
  (if resp
    (assoc resp :body (cheshire/encode body))
    (assoc resp :body "")))

(defn add-headers [resp body]
  (let [cors-headers {"Access-Control-Allow-Origin"  "*"
                      "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"}
        headers (if body
                  (conj cors-headers {"Content-Type" "application/json"})
                  cors-headers)]
    (assoc resp :headers headers)))

(defn resp [status-code body]
  (let [resp {"statusCode"      status-code
              "isBase64Encoded" false}]
    (-> resp
        (add-body body)
        (add-headers body)
        (cheshire/encode))))

(defn decode-body [^InputStream is]
  (try
    (-> is
        (io/reader)
        (cheshire/decode-stream true))
    (catch Exception e
      (log/errorf "Failed to decode the body with exception '%s' \n %s" (.getMessage e) (stack-trace e)))))

(defn read-stub-json []
  (cheshire/decode (slurp (io/resource "stub.json"))))

(defn result-or-error [results]
  (try
    (doall results)
    (catch Exception e
      (log/errorf "Failed to get result: %s \n %s" (.getMessage e) (stack-trace e))
      {:error   true
       :ready   true
       :message (.getMessage e)})))

(defn zip [coll1 coll2]
  (interleave coll1 coll2))

(defn csv-to-map [f]
  (let [raw-csv (csv/read-csv f)]
    (log/debug "Raw CSV: " raw-csv)
    (let [header (vec (->> (first raw-csv)
                           (map keyword)))
          data (rest raw-csv)
          pairs (map #(zip header %) data)]
      (log/debugf "Header: %s" header)
      (doall (map #(apply array-map %) pairs)))))

(defn read-stub-csv []
  (let [data (slurp (io/resource "data-example.csv"))]
    (doall
      (csv-to-map data))))

(defn do-return [func & args]
  (try
    (let [resp (apply func args)]
      (if resp
        (if (contains? resp :error)
          {:status 500
           :body   {:error true :message "ERROR_01"}}
          {:status 200
           :body   resp})
        {:status 404}))
    (catch Exception e
      (log/errorf "Exception caught in utils/do-return '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn do-insert [func & args]
  (try
    (let [id (gen-uuid)
          insert-fn (partial func id)
          resp (apply insert-fn args)]
      {:status 200
       :body   resp})
    (catch Exception e
      (log/errorf "Exception caught in utils/do-insert '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn do-delete [search-fn delete-fn & args]
  (try
    (let [original (apply search-fn args)]
      (apply delete-fn args)
      {:status 200
       :body   original})
    (catch Exception e
      (log/errorf "Exception caught in utils/do-delete '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn do-update [func & args]
  (try
    (let [resp (apply func args)]
      {:status 200
       :body   resp})
    (catch Exception e
      (log/errorf "Exception caught in utils/do-update '%s' \n %s" (.getMessage e) (stack-trace e))
      {:status 500
       :body   {:error   true
                :message (.getMessage e)}})))

(defn add-status [resp-vec]
  {:status (if (every? #(= 200 (get % :status)) resp-vec) 200 500)
   :body   resp-vec})
