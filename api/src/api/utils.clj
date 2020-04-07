(ns api.utils
  (:require [jsonista.core :as json])
  (:import (java.util UUID)))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn read-json-is [is]
  (when (some? is)
    (json/read-value is read-mapper)))
