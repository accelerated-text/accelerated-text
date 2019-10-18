(ns api.nlg.generator.planner-ng
  (:require [api.nlg.generator.grammar :as grammar]
            [api.nlg.generator.ops :as ops]
            [api.nlg.generator.parser-ng :as parser]
            [api.nlg.generator.realizer :as realizer]
            [acc-text.nlg.grammar :as ccg]
            [clojure.tools.logging :as log]))

(defn build-dp-instance
  "dp - a hashmap compiled with `parse-document-plan`
   data - a flat hashmap (represents CSV)
   returns: hashmap (context) which will be used to generate text"
  [dp]
  (loop [context {:dynamic        []
                  :static         []
                  :reader-profile :default}
         fs dp]
    (if (map? fs)
      (ops/merge-context context fs)
      (if (empty? fs)
        context
        (let [[head & tail] fs]
          (log/tracef "Head: %s" head)
          (recur (ops/merge-context context (into {} head)) tail))))))

(defn get-placeholder [item]
  (if (realizer/placeholder? item)
    (get-in item [:name :dyn-name])
    (:item item)))

(defn amr? [item] (get-in item [:attrs :amr] false))

(defn generate-templates
  "Takes context and generates numerous sentences. Picks random one"
  [grammar context]
  (let [dyn-values (map get-placeholder (remove amr? (:dynamic context)))
        values (concat (distinct (:static context)) dyn-values)
        _ (log/debugf "Context: %s" context)
        generated (apply (partial ccg/generate grammar) (ops/distinct-wordlist values))]
    {:context   context
     :templates generated}))

(defn build-segment
  [grammar segment]
  (map (partial generate-templates grammar)
       (map build-dp-instance segment)))

(defn render-segment
  [templates data]
  (let [realized (filter seq (map (partial realizer/realize data) templates))
        _ (log/debugf "Realized: %s" (pr-str realized))
        sentences (map rand-nth realized)]
    (ops/join-sentences sentences)))

(defn render-dp
  "document-plan - a hash map with document plan
   data - list of rows of hashmap (represents CSV)
   reader-profile - key defining reader (user)
   returns: generated text"
  [document-plan data reader-profile]
  (let [dp (parser/parse-document-plan document-plan {} {:reader-profile reader-profile})
        instances (map #(map build-dp-instance %) dp)
        context (ops/merge-contexts {:static [] :dynamic []} instances)
        g (grammar/compile-custom-grammar
            (remove
              (fn [item] (get-in item [:attrs :amr]))
              (:dynamic context)))
        templates (map (partial build-segment g) dp)]
    (log/debugf "Templates: %s" (pr-str templates))
    (map (fn [row] (->> templates
                        (map #(render-segment % row))
                        (ops/join-segments)))
         data)))
