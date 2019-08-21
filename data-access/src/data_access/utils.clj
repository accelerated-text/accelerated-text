(ns data-access.utils
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [clojure.data.csv :as csv]
            [clojure.tools.logging :as log])
  (:import (java.util UUID)))

(defn gen-uuid [] (str (java.util.UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))

(defn csv-to-map
  [f]
  (let [raw-csv (csv/read-csv f)]
    (log/debug "Raw CSV: " raw-csv)
    (let [header (vec  (->> (first raw-csv)
                            (map keyword)))
          data (rest raw-csv)
          pairs (map #(interleave header %) data)]
      (log/debugf "Header: %s" header)
      (doall (map #(apply array-map %) pairs)))))
