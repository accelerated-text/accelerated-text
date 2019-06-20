(ns translate.core
  (:require [clojure.tools.logging :as log]
            [translate.dictionary :as dictionary]))

(defn cleanup
  [d]
  (into {} (remove (fn [[k v]] (nil? v)) d)))

(defn translate-input
  [query variables context]
  (list query variables context))

(declare translate-output-node)

(defn translate-output-array
  [item]
  (log/tracef "Handling array: %s" (pr-str item))
  (cond
    (map? item) (map translate-output-node item)
    (seq? item) (map translate-output-array item)
    :else item))

(defn translate-output-node
  [[k v]]
  (log/tracef "Key: %s Val: %s" k v)
  (case k
    (cond
      (map? v) {k (into (sorted-map) (map translate-output-node v))}
      (seq? v) {k (flatten (map translate-output-array v))}
      :else {k v})))


(defn translate-output
  [resp]
  (into (sorted-map) (map translate-output-node resp)))
