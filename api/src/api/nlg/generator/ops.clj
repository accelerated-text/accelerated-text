(ns api.nlg.generator.ops
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn merge-context
  [{l-static :static l-dynamic :dynamic l-reader-profile :reader-profile :as left}
   {r-static :static r-dynamic :dynamic r-reader-profile :reader-profile }]
  (merge left
         {:static         (concat l-static r-static)
          :dynamic        (concat l-dynamic r-dynamic)
          :reader-profile (or l-reader-profile r-reader-profile)}))

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
  (update context :dynamic (fn [vals] (conj vals {:name  value
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
  {:provides    ["provides"]
   :consequence ["results"]})

(defn get-random [key col]
  (let [words (get col key [])]
    (when (seq words)
      (rand-nth words))))

(defn get-word [word] (get-random (keyword word) synset))

(defn join-words
  [[first-word & last-words :as words]]
  (if (seq last-words)
    (str (string/join ", " (butlast words)) " and " (last words))
    first-word))

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
      (let [[[original replace] & tail] r
            result (try (string/replace text original replace)
                        (catch Exception _ (log/errorf "Problem with: %s -> %s" original replace)))]
        (recur result tail)))))

(defn zip [coll1 coll2]
  (map vector coll1 coll2))

(defn wrap-to [col key]
  (map (fn [item] {key item}) col))
