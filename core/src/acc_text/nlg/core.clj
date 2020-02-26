(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlp.utils :as nlp]
            [acc-text.nlg.enrich.core :as enrich]))

(defn generate-text [semantic-graph {data :data :as context} lang]
  (->> (grammar/build "Default" "Instance" (conditions/select semantic-graph data) context)
       (generator/generate lang)
       (map (comp nlp/annotate nlp/process-sentence))))

(defn enrich-text
  [context text]
  (get (enrich/enrich-request context text) :result text))
