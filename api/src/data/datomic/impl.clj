(ns data.datomic.impl
  (:require [api.config :refer [conf]]
            [data.protocol :as protocol]
            [datomic.api :as d]
            [mount.core :refer [defstate]]
            [clojure.tools.logging :as log]))

(defstate conn
  :start (d/connect (:db-uri conf)))

(defmulti transact-item (fn [resource-type _ _] resource-type))

(defmethod transact-item :data-files [_ key data-item]
  (d/transact conn
              [{:data-file/id       key
                :data-file/filename (:filename data-item)
                :data-file/content  (:content data-item)}]))

(defmethod transact-item :default [resource-type key _]
  (log/warnf "Default implementation of transact-item for the '%s' with key '%s'"
             resource-type key))

(defmulti pull-entity (fn [resource-type _] resource-type))

(defmethod pull-entity :data-files [resource-type key]
  (log/debugf "Pulling entity %s with key %s" resource-type key)
  (let [df (ffirst (d/q '[:find (pull ?e [*])
                          :where
                          [?e :data-file/id ?key]]
                        (d/db conn)))]
    {:content (:data-file/content df)}))

(defmethod pull-entity :default [resource-type key]
  (log/warnf "Default implementation of pull-entity for the '%s' with key '%s'"
             resource-type key))

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
