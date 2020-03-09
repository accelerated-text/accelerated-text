(ns acc-text.nlg.core
  (:require [acc-text.nlg.generator :as generator]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlp.utils :as nlp]
            [acc-text.nlg.enrich.core :as enrich]
            [clojure.tools.logging :as log]))

(defn generate-text [semantic-graph {data :data :as context} lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" context)
  (->> (generator/generate lang)
       (map (comp nlp/annotate nlp/process-sentence))))

(defn enrich-text
  [context text]
  (get (enrich/enrich-request context text) :result text))
