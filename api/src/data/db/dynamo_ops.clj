(ns data.db.dynamo-ops
  (:require [clojure.tools.logging :as log]
            [data.db.config :as config]
            [data.utils :as utils]
            [taoensso.faraday :as far]))

(def tables-conf {:results            config/results-table
                  :data                config/data-table
                  :blockly             config/blockly-table
                  :lexicon             config/lexicon-table
                  :dictionary          config/dictionary-table
                  :dictionary-combined config/dictionary-combined-table
                  :phrase-usage        config/phrase-usage-model-table
                  :phrase              config/phrase-table
                  :reader-flag-usage   config/reader-flag-usage-table
                  :reader-flag         config/reader-flag-table
                  :members             config/amr-member-table
                  :verbclass           config/amr-verbclass-table
                  :data-files          config/data-files-table})

(defn resolve-table [type] (get tables-conf type))

(defprotocol DBAccess
  (read-item [this key])
  (write-item [this key data update-count?])
  (update-item [this key data])
  (delete-item [this key])
  (list-items [this limit])
  (scan-items [this opts])
  (batch-read-items [this opts]))

(defn read! [this key] (read-item this key))
(defn write!
  ([this data]
   (write-item this (utils/gen-uuid) data false))
  ([this key data]
   (write-item this key data false))
  ([this key data update-count?]
   (write-item this key data update-count?)))
(defn update! [this key data] (update-item this key data))
(defn delete! [this key] (delete-item this key))
(defn list! [this limit] (list-items this limit))
(defn scan! [this opts] (scan-items this opts))
(defn batch-read! [this opts] (batch-read-items this opts))

(defn- freeze [data]
  (into {} (map (fn [[k v]]
                  (if (coll? v)
                    {k (far/freeze v)}
                    {k v}))
                data)))

(defn db-access
  [resource-type]
  (let [{table-name :table-name
         table-key  :table-key} (resolve-table resource-type)]
    (reify
      DBAccess
      (read-item [this key]
        (when (some? key)
          (far/get-item (config/client-opts) table-name {table-key key})))
      (write-item [this key data update-count?]
        (let [current-ts (utils/ts-now)
              body       (cond-> (assoc data
                                   table-key key
                                   :createdAt current-ts
                                   :updatedAt current-ts)
                                 update-count? (assoc :updateCount 0))]
          (far/put-item (config/client-opts) table-name (freeze body))
          body))
      (update-item [this key data]
        (when-let [original (far/get-item (config/client-opts) table-name {table-key key})]
          (log/debugf "Updating\n key: '%s' \n content: '%s'" key data)
          (let [body (cond-> (merge original data {:updatedAt (utils/ts-now) table-key key})
                             (contains? original :updateCount) (update :updateCount inc))]
            (log/debugf "Saving updated content: %s" (pr-str body))
            (far/put-item (config/client-opts) table-name (freeze body))
            body)))
      (delete-item [this key]
        (log/debugf "Deleting\n key: '%s'" key)
        (far/delete-item (config/client-opts) table-name {table-key key}))
      (list-items [this limit]
        (far/scan (config/client-opts) table-name {:limit limit}))
      (scan-items [this opts]
        (far/scan (config/client-opts) table-name opts))
      (batch-read-items [this ids]
        (log/debugf "Batch reading keys: %s" (pr-str ids))
        (far/batch-get-item (config/client-opts) {table-name {:prim-kvs {table-key ids}}})))))

(defn- get-table-keys
  [client-opts table-name]
  (mapcat (fn [[k v]]
            (vector k (get v :data-type)))
          (:prim-keys (far/describe-table client-opts table-name))))

(def ignored-tables
  "Ignore data of these tables when cloning"
  #{:blockly-workspace :nlg-results})

(defn clone-tables-to-local-db
  [endpoint-url local-endpoint-url limit]
  (let [client-opts (assoc (config/client-opts) :endpoint endpoint-url)
        local-client-opts {:endpoint local-endpoint-url}]
    (doseq [table (far/list-tables client-opts)]
      (when-not (contains? (set (far/list-tables local-client-opts)) table)
        (log/debugf "Creating local DynamoDB table `%s`" (name table))
        (far/create-table local-client-opts table (get-table-keys client-opts table) {:block? true}))
      (when-not (contains? ignored-tables table)
        (log/debugf "Fetching DynamoDB table `%s` from %s" (name table) (:endpoint client-opts))
        (doseq [item-batch (partition-all 25 (far/scan client-opts table {:limit limit}))]
          (if (> (count item-batch) 1)
            (far/batch-write-item local-client-opts {table {:put (map freeze item-batch)}})
            (far/put-item local-client-opts table (freeze (first item-batch)))))))))
