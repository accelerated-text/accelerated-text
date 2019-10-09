(ns api.nlg.generator.realizer
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(defn placeholder?
  "Checks if item is placeholder inside sentence"
  [{{source :source} :attrs}]
  (case source
    :cell true
    :quote true
    :quotes true
    false))

(defn data-filter [data {:keys [value gate]}]
  (let [result (gate data)]
    (log/debugf "Value: '%s' Data: %s. Passes? %b" value data result)
    result))

(defn get-value
  "Pulls concrete value for item"
  [{{:keys [source gate]} :attrs {:keys [cell quote quotes] :as name} :name} data]
  (when (or (nil? gate) (gate data))
    (case source
      :cell (get data cell)
      :quote quote
      :quotes (some->> quotes
                       (filter (partial data-filter data))
                       (seq)
                       (rand-nth)
                       (get :value))
      name)))

(defn realize-template [placeholders data template]
  (log/tracef "Placeholders: %s data: %s template: %s" (pr-str placeholders) data template)
  (reduce (fn [result {{dyn-name :dyn-name} :name :as placeholder}]
            (if-let [value (get-value placeholder data)]
              (string/replace result dyn-name value)
              result))
          template
          placeholders))

(defn str-realized? [s]
  (zero? (count (re-find #"\$\d+" s))))

(defn realize
  "Takes sentence, context and replaces all placeholders with actual value"
  [data {:keys [context templates]}]
  (let [placeholders (filter placeholder? (:dynamic context))]
    (->> templates
         (map (partial realize-template placeholders data))
         (filter str-realized?))))
