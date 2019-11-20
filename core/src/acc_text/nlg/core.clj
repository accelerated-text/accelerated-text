(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar-impl :as grammar]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.utils.nlp :as nlp]
            [clojure.string :as str]))

(defn realize [text placeholders]
  (when-not (str/blank? text)
    (reduce-kv (fn [s k v]
                 (let [pattern (re-pattern (format "(?i)\\{\\{%s\\}\\}" (name k)))]
                   (str/replace s pattern v)))
               text
               placeholders)))

(defn generate-text [semantic-graph placeholders]
  (->> (get semantic-graph ::sg/graph)
       (grammar/build)
       (generator/generate)
       (map #(realize % placeholders))
       (map nlp/process-sentence)
       (map nlp/annotate)))
