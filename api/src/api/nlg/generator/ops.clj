(ns api.nlg.generator.ops
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn merge-context
  [{l-static :static l-dynamic :dynamic l-reader-profile :reader-profile :as left}
   {r-static :static r-dynamic :dynamic r-reader-profile :reader-profile}]
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
  (update context :static #(conj % value)))

(defn append-dynamic
  [value attrs context]
  (update context :dynamic #(conj % {:name value :attrs attrs})))

(defn distinct-wordlist
  [values]
  (let [wordlists (filter (fn [v] (= :wordlist (get-in v [:attrs :type]))) values)
        other (filter (fn [v] (not= :wordlist (get-in v [:attrs :type]))) values)
        wordlists-grouped (group-by (fn [v] (get-in v [:attrs :class])) wordlists)]
    (log/debugf "Other: %s WordlistGrouped: %s" (pr-str other) (pr-str wordlists-grouped))
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

(defn end-with
  "End text with token if it doesn't end with it already"
  [token text]
  (if-not (string/ends-with? token text)
    (str text token)
    text))

(defn capitalize
  "Similar to `clojure.string/capitalize`. However, clojure util modifies following characters, we don't want that"
  [[first-letter & other]]
  (if-not (empty? other)
    (str (string/upper-case first-letter) (string/join "" other))
    first-letter))

(defn join-sentences
  [sentences]
  (->> (remove nil? sentences)
       (map capitalize)
       (string/join ". ")
       (end-with ".")))

(defn join-segments
  [segments]
  (string/trim (string/join "" segments)))
