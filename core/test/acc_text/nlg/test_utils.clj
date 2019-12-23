(ns acc-text.nlg.test-utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (java.io PushbackReader)))

(defn load-test-semantic-graph [filename]
  (with-open [r (io/reader (format "test/resources/semantic_graphs/%s.edn" filename))]
    (edn/read (PushbackReader. r))))

(defn single-amr-doc-plan [amr-name]
  #:acc-text.nlg.semantic-graph
  {:relations [{:from :01 :to :02 :role :segment}
               {:from :02 :to :03 :role :instance}]
   :concepts  [{:id :01 :type :document-plan}
               {:id :02 :type :segment}
               {:id :03 :type :amr :value amr-name}]})
