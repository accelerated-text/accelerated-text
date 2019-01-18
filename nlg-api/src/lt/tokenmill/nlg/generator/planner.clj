(ns lt.tokenmill.nlg.generator.planner
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.generator.simple-nlg :as nlg]
            [clojure.java.io :as io]
            [cheshire.core :as ch]))

(defn set-subj [selector]  (fn [context data] (assoc context :subj (selector data))))
(defn set-verb-w-selector [selector] (fn [context data] (assoc context :verb (selector data))))
(defn set-verb-static [verb] (fn [context _] (assoc context :verb verb)))
(defn set-obj [selector] (fn [context data] (update context :objs (fn [vals] (conj vals (selector data))))))
(defn set-complement [selector] (fn [context data] (assoc context :complement (selector data))))


(defn node-plus-children [node children]
  (if (not (empty? children))
    (cons node children)
    (list node)))

(defn load-example-dp
  []
  (with-open [in (io/input-stream (io/resource "dp-example.json"))]
    (-> (slurp in)
        (ch/decode true)
        :documentPlan)))

(declare parse-node)

(defn parse-segment
  [node]
  (let [children (node :children)]
    (map parse-node children)))

(defn parse-component
  [node]
  (let [name (parse-node (node :name))
        children (flatten (map parse-node (node :children)))]
    (node-plus-children (set-subj name) children)))

(defn parse-product [node] (parse-component node))

(defn parse-relationship
  [node]
  (let [rel-name (node :relationshipType)
        children (map parse-node (node :children))]
    (node-plus-children (set-verb-static rel-name) (map set-obj children))))

(defn parse-rhetorical [node] ())

(defn parse-cell
  [node]
  (let [cell-name (node :name)]
    (fn [data]
      (let [result (get data cell-name)]
        (log/debugf "Searching for: '%s' in %s. Result: %s" cell-name, data, result)
        result))))

(defn parse-quote
  [node]
  (fn [_] (node :text)))

(defn parse-document-plan
  [node]
  (let [statements (node :statements)]
    (doall (map parse-node statements))))

(defn parse-node
  [node]
  (let [t (keyword (node :type))]
    (case t
      :Document-plan (parse-document-plan node)
      :Segment (parse-segment node)
      :Product (parse-product node)
      :Product-Component (parse-component node)
      :Cell (parse-cell node)
      :Quote (parse-quote node)
      :Relationship (parse-relationship node)
      :Rhetorical (parse-rhetorical node)
      :If-then-else ())))


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
      (let [head (first fs)
            tail (rest fs)
            result (head context data)]
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
  (let [segments (map #(render-segment % data) parse-document-plan document-plan)]
    (string/join "" segments)))
