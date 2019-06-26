(ns data-access.db.dynamo-ops
  (:require [taoensso.faraday :as far]
            [data-access.db.config :as config]
            [data-access.utils :as utils]
            [clojure.tools.logging :as log]))


(defn resolve-table
  [type]
  (case type
    :results config/results-table
    :data config/data-table
    :blockly config/blockly-table
    :lexicon config/lexicon-table
    :dictionary config/dictionary-table
    :dictionary-combined config/dictionary-combined-table
    :phrase-usage config/phrase-usage-model-table
    :phrase config/phrase-table
    :reader-flag-usage config/reader-flag-usage-table
    :reader-flag config/reader-flag-table))

(defprotocol DBAccess
  (read-item [this key])
  (write-item [this key data])
  (update-item [this key data])
  (delete-item [this key])
  (list-items [this limit])
  (scan-items [this opts])
  (batch-read-items [this opts]))

(defn read! [this key] (read-item this key))
(defn write!
  ([this data]
   (write-item this (utils/gen-uuid) data))
  ([this key data]
   (write-item this key data)))
(defn update! [this key data] (update-item this key data))
(defn delete! [this key] (delete-item this key))
(defn list! [this limit] (list-items this limit))
(defn scan! [this opts] (scan-items this opts))
(defn batch-read! [this opts] (batch-read-items this opts))

(defn freeze! [coll] (far/freeze coll))

(defn normalize
  [data]
  (into {}  (map (fn
                    [[k v]]
                    (if (coll? v)
                      {k (freeze! v)}
                      {k v}))
                  data)))

(defn db-access
  [resource-type]
  (let [{table-name :table-name
         table-key :table-key} (resolve-table resource-type)]
    (reify
      DBAccess
      (read-item [this key]
        (far/get-item (config/client-opts) table-name {table-key key}))
      (write-item [this key data]
        (log/debugf "Writing\n key: '%s' \n content: '%s'" key data)
        (let [body (-> data
                       (assoc table-key key)
                       (assoc :createdAt (utils/ts-now))
                       (assoc :updatedAt (utils/ts-now)))
              normalized (doall (normalize body))]
          (do
            (far/put-item (config/client-opts) table-name normalized)
            body)))
      (update-item [this key data]
        (log/debugf "Updating\n key: '%s' \n content: '%s'" key data)
        (let [original (far/get-item (config/client-opts) table-name {table-key key})
              body (-> (merge original data)
                       (assoc :updatedAt (utils/ts-now))
                       (assoc :key key))]
          (do
            (log/debugf "Saving updated content: %s" (pr-str body))
            (far/put-item (config/client-opts) table-name body)
            (far/get-item (config/client-opts) table-name {table-key key}))))
      (delete-item [this key]
        (log/debugf "Deleting\n key: '%s'" key)
        (far/delete-item (config/client-opts) table-name {table-key key}))
      (list-items [this limit]
        (far/scan (config/client-opts) table-name {:limit limit}))
      (scan-items [this opts]
        (far/scan (config/client-opts) table-name opts))
      (batch-read-items [this opts]
        (far/batch-get-item (config/client-opts) {table-name opts})))))

(defn get-workspace
  [key]
  (far/get-item (config/client-opts) config/blockly-table {:id key}))

(defn list-workspaces
  [limit]
  (far/scan (config/client-opts) config/blockly-table {:limit limit}))


(defn write-workspace
  [key workspace]
  (let [body (assoc workspace :id key)]
    (do
      (far/put-item
       (config/client-opts)
       config/blockly-table
       body)
      body)))

(defn add-workspace
  [key workspace]
  (let [body (assoc workspace :createdAt (utils/ts-now))]
    (write-workspace key body)))

(defn update-workspace
  [key workspace]
  (let [original (get-workspace key)
        body (merge
              original
              (assoc workspace :updatedAt (utils/ts-now)))]
    (write-workspace key body)))
  

(defn delete-workspace
  [key]
  (far/delete-item (config/client-opts) config/blockly-table {:id key}))
