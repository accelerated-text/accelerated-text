(ns data.utils
  (:require [clj-yaml.core :as yaml])
  (:import (java.io File)
           (java.util UUID)
           (java.time Instant)))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn ts-now []
  (int (.getEpochSecond (Instant/now))))

(defn read-yaml [^File f]
  (yaml/parse-string (slurp f)))

(defn get-ext [^File f]
  (let [filename (.getName f)]
    (subs filename (.lastIndexOf filename ".") (count filename))))
