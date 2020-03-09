(ns acc-text.nlg.generator.utils
  (:require [clojure.string :as str]))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))
