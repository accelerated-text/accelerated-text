(ns data.test-utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io File PushbackReader)))

(defn read-edn [^File f]
  (with-open [rdr (io/reader f)]
    (edn/read (PushbackReader. rdr))))

(defn read-test-result [name]
  (read-edn (io/file (format "test/resources/results/%s.edn" name))))
