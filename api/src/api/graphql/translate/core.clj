(ns api.graphql.translate.core)

(defn cleanup
  [d]
  (into {} (remove (fn [[_ v]] (nil? v)) d)))

(defn translate-input
  [query variables context]
  (list query variables context))

(defn paginated-response
  [result]
  {:items      result
   :offset     0
   :limit      100
   :totalCount (count result)})
