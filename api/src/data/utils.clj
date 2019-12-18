(ns data.utils
  (:require [clj-yaml.core :as yaml])
  (:import (java.io File)
           (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (long (.getEpochSecond (Instant/now))))

(defn read-yaml [^File f]
  (yaml/parse-string (slurp f)))

(defn get-ext [^File f]
  (let [filename (.getName f)
        index (.lastIndexOf filename ".")]
    (when (not= index -1)
      (subs filename index (count filename)))))

(defn get-name [^File f]
  (let [filename (.getName f)
        index (.lastIndexOf filename ".")]
    (cond-> filename (not= index -1) (subs 0 index))))
