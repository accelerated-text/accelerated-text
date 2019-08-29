(ns data-access.utils
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.tools.logging :as log])
  (:import (java.util UUID)
           (java.time Instant)))

(defn gen-uuid [] (str (java.util.UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))

(defn raw-csv->maps [input]
  (let [[header & data] (csv/read-csv input)]
    (mapv zipmap (repeat header) data)))

(defn ts-to-string [ts]
  (when ts (str (Instant/ofEpochMilli ts))))
