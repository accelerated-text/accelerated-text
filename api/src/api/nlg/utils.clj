(ns api.nlg.utils
  (:require [acc-text.nlg.verbnet.core :as verbnet]
            [cheshire.core :as cheshire]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [data.entities.amr :as amr-entity])
  (:import (java.io InputStream)
           (java.util UUID)))

(defn gen-uuid [] (.toString (UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))

(defn resp [status-code body]
  (let [resp {"statusCode"      status-code
              "isBase64Encoded" false}
        add-body (fn [r]
                   (if body
                     (assoc r :body (cheshire/encode body))
                     (assoc r :body "")))
        add-headers (fn [r]
                      (let [cors-headers {"Access-Control-Allow-Origin"  "*"
                                          "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
                                          "Access-Control-Allow-Headers" "*, Content-Type"}
                            headers (if body
                                      (conj cors-headers ["Content-Type" "application/json"])
                                      cors-headers)]
                        (assoc r :headers headers)))]
    (-> resp
        (add-body)
        (add-headers)
        (cheshire/encode))))

(defn decode-body [^InputStream is]
  (try
    (-> is
        (io/reader)
        (cheshire/decode-stream true))
    (catch Exception e
      (log/errorf "Failed to decode the body with exception '%s'" e))))

(defn read-stub-json [] (cheshire/decode (slurp (io/resource "stub.json"))))

(defn get-stack-trace [e] (string/join "\n" (map str (.getStackTrace e))))

(defn result-or-error
  [results]
  (try
    (doall results)
    (catch Exception e
      (log/errorf "Failed to get result: %s" (get-stack-trace e))
      {:error true :ready true :message (.getMessage e)})))

(defn zip [coll1 coll2]
  (interleave coll1 coll2))

(defn csv-to-map
  [f]
  (let [raw-csv (csv/read-csv f)]
    (log/debug "Raw CSV: " raw-csv)
    (let [header (vec (->> (first raw-csv)
                           (map keyword)))
          data (rest raw-csv)
          pairs (map #(zip header %) data)]
      (log/debugf "Header: %s" header)
      (doall (map #(apply array-map %) pairs)))))

(defn read-stub-csv
  []
  (let [data (slurp (io/resource "data-example.csv"))]
    (doall
      (csv-to-map data))))

(defn do-return
  [func & args]
  (try (if-let [resp (apply func args)]
         (if (contains? resp :error)
           {:status 500 :body {:error true :message "ERROR_01"}}
           {:status 200 :body resp})
         {:status 404})
       (catch Exception e
         (log/error (get-stack-trace e))
         {:status 500
          :body   {:error true :message (.getMessage e)}})))

(defn do-insert
  [func & args]
  (let [id (gen-uuid)
        insert-fn (partial func id)]
    (try (let [resp (apply insert-fn args)]
           {:status 200
            :body   resp})
         (catch Exception e
           (log/error (get-stack-trace e))
           {:status 500
            :body   {:error   true
                     :message (.getMessage e)}}))))

(defn do-delete
  [search-fn delete-fn & args]
  (try (let [original (apply search-fn args)
             _ (apply delete-fn args)]
         {:status 200
          :body   original})
       (catch Exception e
         (log/error e)
         {:status 500
          :body   {:error   true
                   :message (.getMessage e)}})))

(defn do-update
  [func & args]
  (try (let [resp (apply func args)]
         {:status 200
          :body   resp})
       (catch Exception e
         (log/error e)
         {:status 500
          :body   {:error   true
                   :message (.getMessage e)}})))

(defn add-status [resp-vec]
  {:status (if (every? #(= 200 (get % :status)) resp-vec) 200 500)
   :body   resp-vec})

(defn import-verbnet
  []
  (let [data (verbnet/xml->vclass "resources/verbnet/battle.xml")]
    (amr-entity/create-verbclass {:id             (:id data)
                                  :thematic-roles (:thematic-roles data)
                                  :frames         (:frames data)})))
