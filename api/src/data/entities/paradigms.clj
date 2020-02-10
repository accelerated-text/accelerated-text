(ns data.entities.paradigms
  (:require [data.entities.rgl :refer [list-package read-rgl]]))

(defn list-eng-rgl-files []
  (list-package (or (System/getenv "RGL_ENG_GRAMMAR_PACKAGE") "grammar/paradigms-eng.yaml")))

(defn list-ger-rgl-files []
  (list-package (or (System/getenv "RGL_GER_GRAMMAR_PACKAGE") "grammar/paradigms-ger.yaml")))

(defn load-all-eng []
  (mapcat read-rgl (list-eng-rgl-files)))

(defn load-all-ger []
  (mapcat read-rgl (list-ger-rgl-files)))

(defn load-single [id]
  (some #(when (= id (:id %)) %) (concat (list-eng-rgl-files) (list-ger-rgl-files))))
