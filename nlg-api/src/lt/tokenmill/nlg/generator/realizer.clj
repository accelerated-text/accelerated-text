(ns lt.tokenmill.nlg.generator.realizer
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn placeholder?
  "Checks if item is placeholder inside sentence"
  [{attrs :attrs
    name :name}]
  (case (attrs :source)
    :cell true
    :quote true
    false))

(defn get-value
  "Pulls concrete value for item"
  [{attrs :attrs
    name :name}
   data]
  (let [result (case (attrs :source)
                 :cell (get data (name :cell))
                 :quote (name :quote)
                 name)]
    (if (contains? attrs :gate)
      (when ((attrs :gate) data)
        result)
      result)))

(defn realize-template
  "Realizes single template"
  [placeholders data template]
  (loop [result template
         replaces placeholders]
    (if (empty? replaces)
      result
      (let [[head & tail] replaces
            placeholder (get-in head [:name :dyn-name])
            value (get-value head data)]
        (if value
          (recur (str/replace result placeholder value) tail)
          (recur result tail))))))

(defn str-realized?
  [s]
  (let [results (re-find #"\$\d+" s)]
    (= 0 (count results))))


(defn realize
  "Takes sentence, context and replaces all placeholders with actual value"
  [data {context :context
         templates :templates}]
  (let [placeholders (filter placeholder? (context :dynamic))
        realize-fn (partial realize-template placeholders data)]
    (->> templates
         (map realize-fn)
         (filter str-realized?))))
