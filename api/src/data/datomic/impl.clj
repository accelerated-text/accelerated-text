(ns data.datomic.impl
  (:require [api.config :refer [conf]]
            [data.protocol :as protocol]
            [datomic.api :as d]
            [mount.core :refer [defstate]]
            [clojure.tools.logging :as log]))

(defstate conn
  :start (d/connect (:db-uri conf)))

(defn transact-item [resource-type key data-item]
  (d/transact conn
              [{:data-file/id       key
                :data-file/filename (:filename data-item)
                :data-file/content  (:content data-item)}]))

(defn pull-entity [resource-type key]
  (log/trace key)
  (let [df (ffirst (d/q '[:find (pull ?e [*])
                          :where
                          [?e :data-file/id ?key]]
                        (d/db conn)))]
    {:content (:data-file/content df)}))

(defn db-access
  [resource-type config]
  (log/debugf "Datomic for: %s with config %s" resource-type config)
  (reify
    protocol/DBAccess
    (read-item [this key]
      (pull-entity resource-type key))
    (write-item [this key data update-count?]
      (transact-item resource-type key data))
    (update-item [this key data] (prn "updating"))
    (delete-item [this key] (prn "deleting"))
    (list-items [this limit] (prn "list items"))
    (scan-items [this opts] (prn "scan items"))
    (batch-read-items [this ids] (prn "batch read"))))
