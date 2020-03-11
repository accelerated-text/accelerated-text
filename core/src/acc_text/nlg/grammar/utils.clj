(ns acc-text.nlg.grammar.utils
  (:require [clojure.string :as str]))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))
