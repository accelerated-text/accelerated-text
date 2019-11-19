(ns acc-text.nlg.gf.translate-gf
  (:require [acc-text.nlg.gf.grammar :as grammar]))


(defn wrap-abstract [name body]
  (format "abstract %s {\n%s\n}\n" name body))

(defn abstract->gf [{module-name :acc-text.nlg.gf.grammar/module-name}]
  (wrap-abstract module-name ""))

(defn concrete->gf [{abstract :acc-text.nlg.gf.grammar/abstract-grammar} data])
