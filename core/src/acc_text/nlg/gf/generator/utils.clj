(ns acc-text.nlg.gf.generator.utils
  (:require [clojure.string :as str]))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))
