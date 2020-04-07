(ns acc-text.nlg.test-utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io PushbackReader)))

(defn read-edn [f]
  (with-open [r (io/reader f)]
    (edn/read (PushbackReader. r))))

(defn load-test-context [filename]
  (read-edn (io/file (format "test/resources/context/%s.edn" filename))))

(defn load-test-grammar [filename]
  (read-edn (io/file (format "test/resources/grammars/%s.edn" filename))))

(defn load-test-semantic-graph [filename]
  (read-edn (io/file (format "test/resources/semantic-graphs/%s.edn" filename))))

(defn load-test-syntax [filename]
  (slurp (io/file (format "test/resources/syntax/%s.gf" filename))))
