(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
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
  (->> (grammar/build :grammar :1 (conditions/select semantic-graph data) context)
       (generator/generate)
       (map #(realize % data))
       (map nlp/process-sentence)
       (map nlp/annotate)))
