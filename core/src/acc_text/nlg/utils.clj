(ns acc-text.nlg.utils
  (:require [jsonista.core :as json]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))
