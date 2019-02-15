(ns lt.tokenmill.nlg.generator.ops
  (:require [clojure.tools.logging :as log]
            [lt.tokenmill.nlg.generator.simple-nlg :as nlg]))

(defn set-subj [selector]  (fn [context data] (assoc context :subj (selector data))))
(defn set-verb-w-selector [selector] (fn [context data] (assoc context :verb (selector data))))
(defn set-verb-static [verb] (fn [context _] (assoc context :verb verb)))
(defn set-obj [selector] (fn [context data] (update context :objs (fn [vals] (conj vals (selector data))))))
(defn set-complement [selector] (fn [context data] (assoc context :complement (selector data))))

(defn lazy-if
  [condition then-branch else-branch]
  (fn
    [ctx data]
    (if (condition data)
      (then-branch data)
      (when (not (nil? else-branch))
        (else-branch ctx data)))))

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
