(ns acc-text.nlg.utils
  (:require [jsonista.core :as json])
  (:import (java.util UUID)))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(def write-mapper (json/object-mapper {:escape-non-ascii true}))

(defn uuid-str? [x]
  (try
    (some? (UUID/fromString x))
    (catch Exception _
      false)))
