(ns acc-text.nlg.test-utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io PushbackReader)))

(defn load-test-semantic-graph [filename]
  (with-open [r (io/reader (format "test/resources/semantic-graphs/%s.edn" filename))]
    (edn/read (PushbackReader. r))))

(defn load-test-context [filename]
  (with-open [r (io/reader (format "test/resources/context/%s.edn" filename))]
    (edn/read (PushbackReader. r))))
