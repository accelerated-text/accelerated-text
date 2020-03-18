(ns acc-text.nlg.core
  (:require [acc-text.nlg.enrich.core :as enrich]
            [acc-text.nlg.generator :as generator]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.grammar :as grammar]
            [acc-text.nlg.semantic-graph.utils :refer [realizable?]]
            [acc-text.nlp.utils :as nlp]
            [clojure.tools.logging :as log]))

(defn generate-text [semantic-graph context lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" (assoc context :constants {"*Language" lang}))
  (map (comp nlp/annotate nlp/process-sentence)
       (when (realizable? semantic-graph)
         (-> (grammar/build-grammar semantic-graph (assoc context :constants {"*Language" lang}))
             (generator/generate lang)
             (service/request lang)))))

(defn enrich-text
  [context text]
  (get (enrich/enrich-request context text) :result text))
