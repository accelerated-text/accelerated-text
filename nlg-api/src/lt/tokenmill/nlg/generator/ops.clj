(ns lt.tokenmill.nlg.generator.ops
  (:require [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.generator.simple-nlg :as nlg]))

(defn set-subj [selector]  (fn [context data] (assoc context :subj (selector data))))
(defn set-verb-w-selector
  [selector]
  (fn
    [context data]
    (log/debugf "Selector: %s" selector)
    (assoc context :verb (selector data))))

(defn set-verb-static [verb] (fn [context _] (assoc context :verb verb)))
(defn set-obj
  [selector]
  (fn
    [context data]
    (log/debugf "Selector: %s" selector)
    (update context :objs (fn [vals] (conj vals (selector data))))))

(defn set-complement
  [selector]
  (fn
    [context data]
    (log/debugf "Selector: %s" selector)
    (assoc context :complement (selector data))))

(defn fnlist->map
  [initial fns & args]
  (loop [context initial
         fs fns]
    (if (empty? fs)
      context
      (let [head (log/spyf "Resolving %s function " (first fs))
            tail (rest fs)
            result (log/spyf "Result after transform %s " (apply head (cons context args)))]
        (recur (merge context result) tail)))))

(defn lazy-if
  [condition then-branch else-branch]
  (fn
    [ctx data]
    (if (condition data)
      (fnlist->map ctx (log/spyf "Then: %s" then-branch) data)
      (when (not (nil? else-branch))
        ((log/spyf "Else: %s" else-branch) ctx data)))))

(def synset
  {:provides ["provide"]
   :consequence ["results in"]})

(defn get-random [key col]
  (let [words (get col key [])]
    (when (not (empty? words))
      (rand-nth words))))

(defn get-word
  [word]
  (get-random (keyword word) synset))
