
(ns data.utils
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [jsonista.core :as json]
            [clojure.set :as set])
  (:import (java.io File PushbackReader)
           (java.util UUID)
           (java.time Instant)))

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
        index (.lastIndexOf filename ".")]
    (when (not= index -1)
      (subs filename index (count filename)))))

(defn get-name [^File f]
  (let [filename (.getName f)
        index (.lastIndexOf filename ".")]
    (cond-> filename
            (not= index -1) (subs 0 index))))

(defn list-files [path]
  (some->> path
           (io/file)
           (file-seq)
           (filter #(.isFile ^File %))))

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

(defn jaccard-distance [d1 d2]
  (if (not= d1 d2)
    (let [k1 (set d1)
          k2 (set d2)]
      (- 1 (/ (count (set/intersection k1 k2)) (count (set/union k1 k2)))))
    0))

(defn distance-matrix [rows]
  (into
   {}
   (map-indexed
    (fn [id1 r1]
      [id1 (remove
            (fn [[idx _]] (= id1 idx))
            (map-indexed (fn [id2 r2] [id2 (jaccard-distance r1 r2)]) rows))])
    rows)))

(defn select-rows [m rows limit]
  (loop [results (set [])
         next    0]
    (if (or (= limit (count results)) (= (count results) (count rows)))
      (map (fn [r] (nth rows r)) results)
      (let [[k _] (apply max-key second (remove (fn [[idx _]] (contains? results idx)) (get m next)))]
        (recur (conj results k) k)))))
