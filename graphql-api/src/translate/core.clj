(ns translate.core)

(defn translate-input
  [query variables context]
  (list query variables context))


(defn translate-output
  [resp]
  resp)
