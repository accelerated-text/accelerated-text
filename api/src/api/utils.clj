(ns api.utils
  (:require [jsonista.core :as json]
            [clojure.java.io :as io])
  (:import (java.util UUID)))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn gen-uuid []
  (str (UUID/randomUUID)))

(defn read-json-is [is]
  (when (some? is)
    (json/read-value is read-mapper)))

(defn slurp-bytes
  "Slurp the bytes from a slurpable thing"
  [x]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (clojure.java.io/input-stream x) out)
    (.toByteArray out)))
