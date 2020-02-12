(ns data.utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io File PushbackReader)
           (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (long (.getEpochSecond (Instant/now))))

(defn read-edn [^File f]
  (with-open [rdr (io/reader f)]
    (edn/read (PushbackReader. rdr))))

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
