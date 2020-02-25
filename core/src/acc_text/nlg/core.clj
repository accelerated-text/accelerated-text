(ns acc-text.nlg.core
  (:require [acc-text.nlg.gf.generator :as generator]
            [acc-text.nlg.gf.grammar :as grammar]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlg.utils.nlp :as nlp]
            [acc-text.nlg.enrich.core :as enrich]))

(defn select-context [context constants]
  (update context :amr #(reduce-kv (fn [m k v]
                                     (assoc m k (cond-> v
                                                        (contains? v :semantic-graph)
                                                        (update
                                                          :semantic-graph
                                                          (fn [sg]
                                                            (conditions/select sg constants))))))
                                   {}
                                   %)))

(defn generate-text [semantic-graph {data :data :as context} lang]
  (->> (grammar/build "Default" "Instance" (conditions/select semantic-graph data) (select-context context {:lang lang}))
       (generator/generate lang)
       (map (comp nlp/annotate nlp/process-sentence))))

(defn enrich-text
  [context text]
  (get (enrich/enrich-request context text) :result text))
