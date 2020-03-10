(ns acc-text.nlg.core
  (:require [acc-text.nlg.generator :as generator]
            [acc-text.nlg.graph.utils :refer [semantic-graph->ubergraph attach-amrs]]
            [acc-text.nlp.utils :as nlp]
            [acc-text.nlg.enrich.core :as enrich]
            [clojure.tools.logging :as log]
            [acc-text.nlg.utils :as utils]
            [jsonista.core :as json]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.gf.grammar :as grammar]))

(defn generate-text [semantic-graph {data :data :as context} lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" context)
  (map (comp nlp/annotate nlp/process-sentence)
       (-> (grammar/build-grammar semantic-graph context)
           (generator/generate lang)
           (service/request lang))))

(defn enrich-text
  [context text]
  (get (enrich/enrich-request context text) :result text))
