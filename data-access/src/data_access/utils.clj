(ns data-access.utils
  (:require [clj-time.core :as time]
            [clj-time.coerce :as tc])
  (:import (java.util UUID)))

(defn gen-uuid [] (str (java.util.UUID/randomUUID)))

(defn ts-now [] (tc/to-long (time/now)))
