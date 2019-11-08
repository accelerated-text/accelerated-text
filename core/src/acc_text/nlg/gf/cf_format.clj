(ns acc-text.nlg.gf.cf-format
  (:require [clojure.string :as string]))

(defn data-morphology-value [value] (format "{{%s}}" (string/upper-case value)))

(defn gf-syntax-item [syntactic-function category syntax]
  (format "%s. %s ::= %s;" syntactic-function category syntax))

(defn gf-modified-morph-item [syntactic-function category modify-syntax item]
  (format "%s. %s ::= %s \"%s\";" (string/capitalize syntactic-function) category modify-syntax (data-morphology-value item)))

(defn gf-morph-item [syntactic-function category syntax]
  (format "%s. %s ::= \"%s\";" (string/capitalize syntactic-function) category syntax))

(defn write-grammar
  "Debug function to spit grammar to a file"
  [rgl file-name]
  (let [out-file (format "grammars/gf/%s.cf" file-name)]
    (clojure.java.io/delete-file out-file true)
    (doseq [item rgl]
      (spit out-file (str item "\n") :append true))))
