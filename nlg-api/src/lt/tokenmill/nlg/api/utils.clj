(ns lt.tokenmill.nlg.api.utils
  (:require [cheshire.core :as cheshire]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clj-time.core :as time]
            [clj-time.coerce :as tc])
  (:import (java.io InputStream))
  (:import (java.util.UUID)))

(defn gen-uuid [] (.toString (java.util.UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))

(defn resp [status-code body]
  (let [resp {"statusCode" status-code
              "body" (cheshire/encode body)
              "isBase64Encoded" false
              "headers" {"Content-Type" "application/json"
                         "Access-Control-Allow-Origin" "*"}}]
    (cheshire/encode resp)))

(defn decode-body [^InputStream is]
  (try
    (-> is
        (io/reader)
        (cheshire/decode-stream true))
    (catch Exception e
      (log/errorf "Failed to decode the body with exception '%s'" e))))

(defn read-stub-json
  []
  (cheshire/decode (slurp (io/resource "stub.json"))))

(defn result-or-error
  [fun & args]
  (try (let [result (apply fun args)]
         result)
       (catch Exception e ({:error true
                            :message (.getMessage e)}))))

(defn do-insert
  [func & args]
  (let [id (gen-uuid)
        insert-fn (partial func id)]
    (try (let [resp (apply insert-fn args)]
           {:status 200
            :body resp})
         (catch Exception e (do
                              (log/error e)
                              {:status 500
                               :body {:error true
                                      :message (.getMessage e)}})))))

(defn do-update
  [func & args]
  (try (let [resp (apply func args)]
         {:status 200
          :body resp})
       (catch Exception e (do
                            (log/error e)
                            {:status 500
                             :body {:error true
                                    :message (.getMessage e)}}))))

