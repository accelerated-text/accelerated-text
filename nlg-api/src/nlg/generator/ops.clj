(ns nlg.generator.ops
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [ccg-kit.verbnet.ccg :refer :all]
            [ccg-kit.spec.morphology :as morph-spec]
            [ccg-kit.spec.lexicon :as lex-spec]
            [ccg-kit.grammar-generation.xml-utils :as u]
            [ccg-kit.grammar-generation.morphology :as m]
            [ccg-kit.grammar-generation.lexicon :as l]
            [clojure.data.xml :as xml]))

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

(defn static-selector
  [value]
  (fn [_] value))

(defn merge-context
  [left right]
  (log/tracef "Trying to merge: \n%s\nand\n%s" left right)
  (let [new-ctx {:static (concat (left :static) (right :static))
                 :dynamic (concat (left :dynamic) (right :dynamic))

                 :reader-profile (or (left :reader-profile) (right :reader-profile))}]
    (log/spyf :trace "Merge result: %s"(merge left new-ctx))))

(defn merge-contexts
  [root other]
  (loop [ctx root
         children other]
    (let [[head & tail] children]
      (if (empty? children)
        ctx
        (recur (merge-context ctx (into {} head)) tail)))))
    

(defn append-static
  [value context]
  (update context :static (fn [vals] (conj vals value))))

(defn append-dynamic
  [value attrs context]
  (update context :dynamic (fn [vals] (conj vals {:name value
                                                  :attrs attrs}))))

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
  {:provides ["provides"]
   :consequence ["results"]})

(defn get-random [key col]
  (let [words (get col key [])]
    (when (not (empty? words))
      (rand-nth words))))

(defn get-word
  [word]
  (get-random (keyword word) synset))

(defn join-words
  [words]
  (if (= 1 (count words))
    (first words)
    (let [head (string/join ", " (butlast words))
          tail (last words)]
      (string/join " " [head "and" tail]))))


(defn distinct-wordlist
  [values]
  (let [wordlists (filter (fn [v] (= :wordlist (get-in v [:attrs :type]))) values)
        other (filter (fn [v] (not (= :wordlist (get-in v [:attrs :type])))) values)
        wordlists-grouped (group-by (fn [v] (get-in v [:attrs :class])) wordlists)
        _ (log/debugf "Other: %s WordlistGrouped: %s" (pr-str other) (pr-str wordlists-grouped))]
    (concat other (map (fn [[_ v]] (rand-nth v)) wordlists-grouped))))

(defn replace-multi
  [original replaces]
  (loop [text original
         r replaces]
    (if (empty? r)
      text
      (let [[head & tail] r
            result (try (string/replace text (:original head) (:replace head))
                        (catch Exception ex (log/errorf "Problem with: %s" (pr-str head))))]
        (recur result tail)))))
