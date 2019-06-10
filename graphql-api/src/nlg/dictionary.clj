(ns nlg.dictionary
  (:require [clojure.tools.logging :as log]
            [db.dynamo-ops :as ops]
            [nlg.utils :as utils]))

(defn process-search-response [{:keys [status body] :as resp} error-return-value]
  (if (= 200 status)
    body
    (do (log/errorf "Failed to fetch dictionary items with response '%s'" resp)
        error-return-value)))

(defn list-dictionary-items []
  (-> (partial ops/list! (ops/db-access :dictionary))
      (utils/do-return nil)
      (process-search-response [])))

(defn dictionary-item [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :dictionary))
      (utils/do-return {:id id})
      (process-search-response {})))

(defn phrase-usage-models [{:keys [ids]}]
  (-> (partial ops/batch-read! (ops/db-access :phrase-usage))
      (utils/do-return {:prim-kvs {:id ids}})
      (process-search-response [])))

(defn reader-flag-usages [{:keys [ids]}]
  (-> (partial ops/batch-read! (ops/db-access :reader-flag-usage))
      (utils/do-return {:prim-kvs {:id ids}})
      (process-search-response [])))

(defn reader-flag [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :reader-flag))
      (utils/do-return {:id id})
      (process-search-response {})))
