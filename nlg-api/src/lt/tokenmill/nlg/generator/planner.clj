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

(defn node-plus-children [node children] (cons node children))

(defn normalize-context
  "Build proper context for our segment builder"
  [context]
  context)

(defn compile-attribute-selector
  "Defines what attribute to select from CSV"
  [value]
  (let [attr-name (value :attribute)]
    (fn [data]
      (get data attr-name))))

(defn compile-random-quote-selector
  "Selects random attribute from list each time"
  [value]
  (let [quotes (map #(% :quote) (value :quotes))]
    (fn [_] (rand-nth quotes))))

(defn compile-elaborate
  "Adds an elaboration quote at the end of sentence"
  [value]
  (when value
    (let [type (value :type)
          selector (case type
                     "Attribute" (compile-attribute-selector value)
                     "Any-of" (compile-random-quote-selector value))]
      (set-complement selector))))

(defn compile-purpose
  "it can be either single attribute, or a list (strict order, random order)"
  [purpose]
  (let [rel-name (purpose :relationship)
        value (purpose :value)
        type (value :type)
        children (case type
                   "Attribute" (list (set-obj (compile-attribute-selector value)))
                   "All" (map #(set-obj (compile-attribute-selector %)) (value :attributes))
                   "Any-of" (list (set-complement (compile-random-quote-selector value))))]
    (conj children (set-verb-static rel-name))))

(defn compile-purposes
  "purposes - list of relations and elaborations" 
  [purposes]
  (when (not (nil? purposes))
    (map compile-purpose purposes)))

(defn compile-component
  "Component - Product or Product's Component element"
  [component]
  (let [purposes  (compile-purposes (component :purposes))
        elaborate (compile-elaborate (component :elaborate))]
    (concat (remove
             nil?
             (list
              (set-subj
               (compile-attribute-selector (component :name)))
              elaborate))
            (flatten purposes))))


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


(defn compile-dp
  "document-plan - a hashmap representing document plan
   returns: a list of lists of functions. Each list represents single sentence"
  [document-plan]
  (let [items (document-plan :items)]
    (doall (map compile-component items))))


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

(defn render-dp
  "document-plan - a hash map with document plan
   data - a flat hashmap (represents CSV)
   returns: generated text"
  [document-plan data]
  (let [plans (compile-dp document-plan)
        instances (map #(build-dp-instance % data) plans)
        sentences (map generate-sentence instances)]
    (log/debug "Plans for building string: " plans)
    (string/join " " sentences)))
