(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar.cf :as cf-grammar]
            [acc-text.nlg.gf.grammar.gf :as gf-grammar]
            [acc-text.nlg.gf.grammar.concrete :as concrete-grammar]
            [acc-text.nlg.gf.grammar.abstract :as abstract-grammar]
            [acc-text.nlg.utils.nlp :as nlp]
            [clojure.string :as str]))

(defn realize [text placeholders]
  (when-not (str/blank? text)
    (reduce-kv (fn [s k v]
                 (let [pattern (re-pattern (format "(?i)\\{\\{%s\\}\\}" (name k)))]
                   (str/replace s pattern v)))
               text
               placeholders)))

(defn generate-text [semantic-graph context data]
  (let [parent-name (:acc-text.nlg.semantic-graph/id semantic-graph)
        abstract (abstract-grammar/build sematic-graph)
        concrete (concrete-grammar/build parent-name (format "%s-1" parent-name) sematic-graph context)])
  (->> (generator/generate parent-name abstract (list [1 concrete]))
       (map #(realize % data))
       (map nlp/process-sentence)
       (map nlp/annotate)))
