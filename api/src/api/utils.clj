(ns api.utils
  (:require [clj-time.coerce :as tc]
            [clj-time.core :as time]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.walk :as walk]
            [jsonista.core :as json])
  (:import (java.net URLDecoder)
           (java.nio.charset Charset)
           (java.util UUID)))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (tc/to-long (time/now)))

(defn split-param [param]
  (take 2 (concat (str/split param #"=") (repeat ""))))

(defn url-decode
  ([string]
   (url-decode string "UTF-8"))
  ([string encoding]
   (when (some? string)
     (URLDecoder/decode string encoding))))

(defn query->map [query-string]
  (when-not (str/blank? query-string)
    (some->> (str/split query-string #"&")
             (seq)
             (mapcat split-param)
             (map url-decode)
             (apply hash-map)
             (walk/keywordize-keys))))

(defn read-json-is [is]
  (when (some? is)
    (json/read-value is read-mapper)))

(defn read-json-os [os]
  (try
    (some-> os
            (.toByteArray)
            (String. (Charset/defaultCharset))
            (json/read-value read-mapper))
    (catch Exception e
      (log/errorf "Failed to decode the body with exception '%s'" e))))

(defn parse-path [uri]
  (let [matcher (re-matcher #"(?<namespace>(\/(\w|[-])+))\/?(?<id>((\w|[-])+))?\/?(?<file>((\w+|[-])+\.\w+))?" uri)
        _ (re-find matcher)
        namespace (.group matcher "namespace")
        id (.group matcher "id")
        file (.group matcher "file")]
    {:namespace   (str/lower-case namespace)
     :path-params (cond
                    (some? file) {:user id :file file}
                    (some? id) {:id id}
                    :else {})}))

(defn get-stack-trace [e]
  (str/join "\n" (map str (.getStackTrace e))))

(defn csv-to-map
  [f]
  (let [raw-csv (csv/read-csv f)]
    (log/debug "Raw CSV: " raw-csv)
    (let [header (->> raw-csv (first) (map keyword) (vec))
          data (rest raw-csv)
          pairs (map #(interleave header %) data)]
      (log/debugf "Header: %s" header)
      (doall (map #(apply array-map %) pairs)))))

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
