(ns graphql.domain.document-plan
  (:require [clojure.tools.logging :as log]
            [db.dynamo-ops :as ops]
            [nlg.utils :as utils]))

(defn process-response [{:keys [status body] :as resp} error-return-value]
  (if (= 200 status)
    body
    (do (log/errorf "Failed to process `DictionaryItem` or its components response '%s'" resp)
        error-return-value)))

(defn paginated-response
  [result]
  {:items result
   :offset 0
   :limit 100
   :totalCount (count result)})

(defn document-plans [_ _ _]
  (-> (partial ops/list! (ops/db-access :blockly))
      (utils/do-return nil)
      (process-response {})
      (paginated-response)))

(defn document-plan [_ arguments _]
  )
