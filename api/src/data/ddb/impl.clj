(ns data.ddb.impl
  (:require [clojure.tools.logging :as log]
            [data.utils :as utils]
            [data.protocol :as protocol]
            [taoensso.faraday :as far]))

(def tables-conf
  {:results             {:table-name "nlg-results"
                         :table-key  :key}
   :data                {:table-name "data"
                         :table-key  :key}
   :blockly             {:table-name "blockly-workspace"
                         :table-key  :id}
   :lexicon             {:table-name "lexicon"
                         :table-key  :key}
   :dictionary          {:table-name "dictionary"
                         :table-key  :id}
   :dictionary-combined {:table-name "dictionary-combined"
                         :table-key  :key}
   :phrase-usage        {:table-name "phrase-usage-model"
                         :table-key  :prim-kvs}
   :phrase              {:table-name "phrase"
                         :table-key  :id}
   :reader-flag-usage   {:table-name "reader-flag-usage"
                         :table-key  :prim-kvs}
   :reader-flag         {:table-name "reader-flag"
                         :table-key  :id}
   :members             {:table-name "amr-members"
                         :table-key  :id}
   :verbclass           {:table-name "amr-verbclass"
                         :table-key  :id}
   :data-files          {:table-name "data-files"
                         :table-key  :id}})

(defn resolve-table [type] (get tables-conf type))

(defn- freeze [data]
  (into {} (map (fn [[k v]]
                  (if (coll? v)
                    {k (far/freeze v)}
                    {k v}))
                data)))

(defn client-opts []
  {:endpoint (or (System/getenv "DYNAMODB_ENDPOINT") "http://localhost:8000")})

(defn db-access
  [resource-type _]
  (let [{table-name :table-name
         table-key  :table-key} (resolve-table resource-type)
        client-ops (client-opts)]
    (reify
      protocol/DBAccess
      (read-item [this key]
        (when (some? key)
          (far/get-item client-ops table-name {table-key key})))
      (write-item [this key data update-count?]
        (let [current-ts (utils/ts-now)
              body (cond-> (assoc data
                             table-key key
                             :createdAt current-ts
                             :updatedAt current-ts)
                           update-count? (assoc :updateCount 0))]
          (far/put-item client-ops table-name (freeze body))
          body))
      (update-item [this key data]
        (when-let [original (far/get-item client-ops table-name {table-key key})]
          (log/debugf "Updating\n key: '%s' \n content: '%s'" key data)
          (let [body (cond-> (merge original data {:updatedAt (utils/ts-now) table-key key})
                             (contains? original :updateCount) (update :updateCount inc))]
            (log/debugf "Saving updated content: %s" (pr-str body))
            (far/put-item client-ops table-name (freeze body))
            body)))
      (delete-item [this key]
        (log/debugf "Deleting\n key: '%s'" key)
        (far/delete-item client-ops table-name {table-key key}))
      (list-items [this limit]
        (far/scan client-ops table-name {:limit limit}))
      (scan-items [this opts]
        (far/scan client-ops table-name opts))
      (batch-read-items [this ids]
        (log/debugf "Batch reading keys: %s" (pr-str ids))
        (far/batch-get-item client-ops {table-name {:prim-kvs {table-key ids}}})))))
