(ns lt.tokenmill.nlg.generator.planner
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.generator.parser :as parser]
            [lt.tokenmill.nlg.generator.simple-nlg :as nlg]))



(defn build-dp-instance
  "dp - a hashmap compiled with `compile-dp`
   data - a flat hashmap (represents CSV)
   returns: hashmap (context) which will be used to generate text"
  [dp data]
  (loop [context {:subj nil
                  :objs []
                  :verb nil
                  :adverb nil}
         fs dp]
    (if (empty? fs)
      context
      (let [head (log/spyf "Resolving %s function " (first fs))
            tail (rest fs)
            _ (log/debugf "Fn: %s Context: %s Data: %s" head context data)
            result (log/spyf "Result after transform %s " (head context data))]
        (recur (merge context result) tail)))))

(defn generate-sentence
  "Takes context and creates sentence"
  [context]
  (let [gen (nlg/generator)]
    (gen
     (fn
       [clause factory]
       (do
         (nlg/add-subj clause (context :subj))
         (nlg/add-verb clause (context :verb))
         (nlg/add-obj clause (nlg/concat-multi
                              factory
                              (nlg/create-multi-nouns
                               factory
                               (context :adverb)
                               (context :objs))))
         (nlg/add-complement clause (context :complement)))))))


(defn render-segment
  [segment data]
  (let [instances (map #(build-dp-instance % data) segment)
        sentences (map generate-sentence instances)]
    (log/debug "Plans for building string: " segment)
    (string/join " " sentences)))

(defn render-dp
  "document-plan - a hash map with document plan
   data - a flat hashmap (represents CSV)
   returns: generated text"
  [document-plan data]
  (let [segments (map #(render-segment % data) (parser/parse-document-plan document-plan))]
    (string/trim (string/join "" segments))))
