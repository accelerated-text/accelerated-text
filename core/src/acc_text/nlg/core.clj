(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.service :as gf-service]
            [acc-text.nlg.grammar :as grammar]
            [clojure.tools.logging :as log]
            [acc-text.nlp.utils :as nlp]))

(defn generate-text [semantic-graph context lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" (assoc context :constants {"*Language" lang}))
  (map (fn [{:keys [text]}]
         {:text     text
          :language lang
          :tokens   (nlp/annotate text)})
       (-> semantic-graph
           (grammar/build-grammar (assoc context :constants {"*Language" lang}))
           (gf-service/request lang))))
