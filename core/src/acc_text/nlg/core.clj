(ns acc-text.nlg.core
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.gf.service :as gf-service]
            [acc-text.nlg.grammar :as grammar]
            [clojure.tools.logging :as log]
            [acc-text.nlp.utils :as nlp]))

(defn build-context [context lang]
  (-> context
      (update :amr #(zipmap (map ::sg/id %) %))
      (assoc :constants {"*Language" lang})
      (update :dictionary #(zipmap (map (fn [{::dictionary-item/keys [key category]}]
                                          [key category])
                                        %)
                                   %))))

(defn generate-text [semantic-graph context lang]
  (log/debugf "Processing generate request for `%s`..." lang)
  (log/debugf "Semantic graph: %s" semantic-graph)
  (log/debugf "Context: %s" context)
  (map (fn [{:keys [text]}]
         {:text     text
          :language lang
          :readers  (:readers context)
          :tokens   (nlp/annotate text)})
       (-> semantic-graph
           (grammar/build-grammar (build-context context lang))
           (gf-service/request lang))))
