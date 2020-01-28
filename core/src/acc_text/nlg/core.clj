(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlg.utils.nlp :as nlp]
            [clojure.string :as str]))

(defn generate-text [semantic-graph {data :data :as context}]
  (let [pruned-sg (conditions/select semantic-graph data)]
    (->> (grammar/build "Grammar" "Instance" pruned-sg context)
         (generator/generate)
         (map (comp nlp/annotate nlp/process-sentence)))))
