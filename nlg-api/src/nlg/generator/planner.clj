(ns nlg.generator.planner
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [nlg.generator.parser :as parser]
            [nlg.generator.simple-nlg :as nlg]
            [ccg-kit.grammar :as ccg]
            [data-access.db.s3 :as s3]
            [data-access.db.config :as config]))

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

(defn download-grammar
  "Downloads latest version of Grammar and returns it's path"
  []
  (do
    (.mkdir (java.io.File. "/tmp/ccg"))
    (.mkdir (java.io.File. "/tmp/ccg/grammar/"))
    (s3/download-dir config/grammar-bucket "grammar" "/tmp/ccg/")
    "/tmp/ccg/grammar/grammar.xml")) 

(defn generate-sentence-ccg
  "Takes context and generates numerous sentences. Picks random one"
  [context]
  (let [values (flatten (vals context))
        _ (log/debugf "Context: %s" context)
        _ (log/debugf "CCG generation using: %s" (list values))
        results (apply (partial ccg/generate (ccg/load-grammar (download-grammar))) values)]
    (if (seq results)
      (rand-nth results)
      "")))


(defn render-segment
  [segment data ccg?]
  (let [instances (map #(build-dp-instance % data) segment)
        sent-fn (if ccg? generate-sentence-ccg generate-sentence)
        sentences (map sent-fn instances)]
    (log/debug "Plans for building string: " segment)
    (string/join " " sentences)))

(defn render-dp
  "document-plan - a hash map with document plan
   data - a flat hashmap (represents CSV)
   returns: generated text"
  [document-plan data ccg?]
  (let [segments (map #(render-segment % data ccg?) (parser/parse-document-plan document-plan))]
    (string/trim (string/join "" segments))))
