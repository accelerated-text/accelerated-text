(ns api.utils
  (:require [clojure.data.csv :as csv]
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

(defn csv-to-map [f]
  (when-not f
    (log/warnf "CSV is NULL" f))
  (let [raw-csv (csv/read-csv (or f ""))
        header (->> raw-csv (first) (map keyword) (vec))
        data (rest raw-csv)
        pairs (map #(interleave header %) data)]
    (doall (map #(apply array-map %) pairs))))
