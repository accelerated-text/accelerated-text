(ns api.nlg.generator.realizer
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn placeholder?
  "Checks if item is placeholder inside sentence"
  [{attrs :attrs}]
  (case (attrs :source)
    :cell true
    :quote true
    :quotes true
    false))

(defn data-filter
  [data {:keys [value gate]}]
  (log/debugf "Value: '%s' Data: %s. Passes? %b" value data (gate data))
  (gate data))

(defn get-value
  "Pulls concrete value for item"
  [{attrs :attrs
    name  :name}
   data]
  (let [result (case (attrs :source)
                 :cell (get data (name :cell))
                 :quote (name :quote)
                 :quotes (some->> (name :quotes)
                                  (filter (partial data-filter data))
                                  (seq)
                                  (rand-nth)
                                  :value)
                 name)]
    (if (contains? attrs :gate)
      (when ((attrs :gate) data)
        result)
      result)))

(defn realize-template
  "Realizes single template"
  [placeholders data template]
  (log/tracef "Placeholders: %s data: %s template: %s" (pr-str placeholders) data template)
  (loop [result template
         replaces placeholders]
    (log/debugf "Template: %s realizing with data: %s" result (pr-str replaces))
    (if (empty? replaces)
      result
      (let [[head & tail] replaces
            placeholder (get-in head [:name :dyn-name])
            value (get-value head data)]
        (if value
          (recur (string/replace result placeholder value) tail)
          (recur result tail))))))

(defn str-realized?
  [s]
  (let [results (re-find #"\$\d+" s)]
    (zero? (count results))))

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

(defn realize
  "Takes sentence, context and replaces all placeholders with actual value"
  [data {context   :context
         templates :templates}]
  (let [placeholders (filter placeholder? (context :dynamic))
        realize-fn (partial realize-template placeholders data)]
    (->> templates
         (map realize-fn)
         (filter str-realized?))))
