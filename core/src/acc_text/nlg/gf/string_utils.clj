(ns acc-text.nlg.gf.string-utils
  (:require [clojure.string :as string]))

(defn escape-string
  "Ensure that resulting value string can be embeded in GF grammars"
  [txt] (string/replace txt #"\"" "\\\\\""))
