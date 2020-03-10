(ns acc-text.nlg.grammar
  (:require [acc-text.nlg.grammar.impl :as impl]))

(defn build-grammar [semantic-graph context]
  (impl/build-grammar semantic-graph context))
