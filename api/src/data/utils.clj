(ns data.utils
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [jsonista.core :as json])
  (:import (clojure.lang PersistentHashSet)
           (java.io File PushbackReader)
           (java.util UUID)
           (java.time Instant)
           (org.apache.commons.codec.digest MurmurHash3)))

(def char-list (map char (concat (range 65 91) (range 97 123))))

(defn gen-rand-str [len]
  (apply str (take len (repeatedly #(rand-nth char-list)))))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (long (.getEpochSecond (Instant/now))))

(def object-mapper
  (json/object-mapper {:decode-key-fn true}))

(defn read-edn [^File f]
  (with-open [rdr (io/reader f)]
    (edn/read (PushbackReader. rdr))))


(defn read-csv [^File f]
  (with-open [reader (io/reader f)]
    (doall
     (csv/read-csv reader))))

(defn read-json [^File f]
  (with-open [f (io/reader f)]
    (json/read-value f object-mapper)))

(defn read-json-str [s]
  (json/read-value s object-mapper))

(defn get-ext [^File f]
  (let [filename (.getName f)
        index    (.lastIndexOf filename ".")]
    (when (not= index -1)
      (subs filename index (count filename)))))

(defn get-name [^File f]
  (let [filename (.getName f)
        index    (.lastIndexOf filename ".")]
    (cond-> filename
      (not= index -1) (subs 0 index))))

(defn file-with-ext
  [^File f ^PersistentHashSet ext]
  (and (some? f) (.isFile f)
       (or (empty? ext)
           (contains? ext (get-ext f)))))

(defn list-files
  ([path] (list-files path #{}))
  ([path ^PersistentHashSet ext]
   (some->> path
            (io/file)
            (file-seq)
            (filter #(file-with-ext % ext)))))

(defn list-files-in-dir
  ([path] (list-files-in-dir path #{}))
  ([path ^PersistentHashSet ext]
   (some->> path
            (io/file)
            (.listFiles)
            (filter #(file-with-ext % ext)))))

(defn list-directories [path]
  (some->> path
           (io/file)
           (file-seq)
           (filter #(.isDirectory ^File %))
           (rest)))

(defn add-ns-to-map [ns m]
  (reduce-kv (fn [ns-m k v]
               (assoc ns-m (keyword ns (name k)) v))
             {}
             m))

(defn murmur-hash [key] (first (MurmurHash3/hash128x64 (.getBytes key))))