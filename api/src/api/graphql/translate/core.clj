(ns api.graphql.translate.core)

(defn paginated-response [result]
  {:items      result
   :offset     0
   :limit      100
   :totalCount (count result)})
