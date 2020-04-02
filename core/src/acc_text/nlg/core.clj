(ns acc-text.nlg.core
  (:require [acc-text.nlg.generator :as generator]
            [acc-text.nlg.gf.service :as service]
            [acc-text.nlg.grammar :as grammar]
            [acc-text.nlp.utils :as nlp]
            [clojure.tools.logging :as log]))

(defn generate-text [semantic-graph context lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" (assoc context :constants {"*Language" lang}))
  (map #(let [text (nlp/process-sentence %)]
          {:text text :language lang :tokens (nlp/annotate text)})
       (-> (grammar/build-grammar semantic-graph (assoc context :constants {"*Language" lang}))
           (generator/generate lang)
           (service/request lang))))
