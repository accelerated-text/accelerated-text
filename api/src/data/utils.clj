(ns data.utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [jsonista.core :as json])
  (:import (java.io File PushbackReader)
           (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (long (.getEpochSecond (Instant/now))))

(def object-mapper
  (json/object-mapper {:decode-key-fn true}))

(defn read-edn [^File f]
  (with-open [rdr (io/reader f)]
    (edn/read (PushbackReader. rdr))))

(defn read-json [^File f]
  (with-open [f (io/reader f)]
    (json/read-value f object-mapper)))

(defn get-ext [^File f]
  (let [filename (.getName f)
        index (.lastIndexOf filename ".")]
    (when (not= index -1)
      (subs filename index (count filename)))))

(defn get-name [^File f]
  (let [filename (.getName f)
        index (.lastIndexOf filename ".")]
    (cond-> filename
            (not= index -1) (subs 0 index))))

(defn list-files [path]
  (->> path
       (io/file)
       (file-seq)
       (filter #(.isFile ^File %))))

(defn list-directories [path]
  (->> path
       (io/file)
       (file-seq)
       (filter #(.isDirectory ^File %))
       (rest)))
