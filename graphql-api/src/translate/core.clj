(ns translate.core
  (:require [clojure.tools.logging :as log]))

(defn translate-input
  [query variables context]
  (list query variables context))

(declare translate-output-node)

(defn translate-output-array
  [item]
  (log/debugf "Handling array: %s" (pr-str item))
  (cond
    (map? item) (map translate-output-node item)
    (seq? item) (map translate-output-array item)
    :else item))

(defn translate-output-node
  [[k v]]
  (log/debugf "Key: %s Val: %s" k v)
  (case k
    :phrases (log/spyf "Handling phrases" {k v})
    (cond
      (map? v) {k (into {} (map translate-output-node v))}
      (seq? v) {k (flatten (map translate-output-array v))}
      :else {k v})))


(defn translate-output
  [resp]
  (into {} (map translate-output-node resp)))
