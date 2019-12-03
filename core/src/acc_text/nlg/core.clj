(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [acc-text.nlg.utils.nlp :as nlp]
            [clojure.string :as str]))

(defn realize [text placeholders]
  (when-not (str/blank? text)
    (reduce-kv (fn [s k v]
                 (let [pattern (re-pattern (format "(?i)\\{\\{%s\\}\\}" (name k)))]
                   (str/replace s pattern v)))
               text
               placeholders)))

(defn prune-semantic-graph [semantic-graph data]
  (-> semantic-graph
      (conditions/select data)
      (sg-utils/prune-unrelated-branches)))

(defn generate-text [semantic-graph context data]
  (->> (grammar/build :grammar :1 (prune-semantic-graph semantic-graph data) context)
       (generator/generate)
       (map #(realize % data))
       (map nlp/process-sentence)
       (map nlp/annotate)))
