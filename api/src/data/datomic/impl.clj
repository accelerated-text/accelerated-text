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
             resource-type key)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defmulti pull-entity (fn [resource-type _] resource-type))

(defmethod pull-entity :data-files [_ key]
  (log/trace key)
  (let [df (ffirst (d/q '[:find (pull ?e [*])
                          :where
                          [?e :data-file/id ?key]]
                        (d/db conn)))]
    {:filename (:data-file/filename df)
     :content (:data-file/content df)}))

(defmethod pull-entity :default [resource-type key]
  (log/warnf "Default implementation of pull-entity for the '%s' with key '%s'"
             resource-type key)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defmulti pull-n (fn [resource-type _] resource-type))

(defmethod pull-n :data-files [_ limit]
  (let [resp (first (d/q '[:find (pull ?e [*])
                           :where [?e :data-file/id]]
                         (d/db conn)))]

    (map (fn [df] {:id       (:data-file/id df)
                   :filename (:data-file/filename df)
                   :content  (:data-file/content df)}) (take limit resp))))

(defmethod pull-n :default [resource-type limit]
  (log/warnf "Default implementation of list-items for the '%s' with key '%s'"
             resource-type limit)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defn db-access
  [resource-type config]
  (log/debugf "Datomic for: %s with config %s" resource-type config)
  (reify
    protocol/DBAccess
    (read-item [this key]
      (pull-entity resource-type key))
    (write-item [this key data update-count?]
      (transact-item resource-type key data))
    (update-item [this key data] (throw (RuntimeException. "NOT IMPLEMENTED")))
    (delete-item [this key] (throw (RuntimeException. "NOT IMPLEMENTED")))
    (list-items [this limit] (pull-n resource-type limit))
    (scan-items [this opts] (throw (RuntimeException. "NOT IMPLEMENTED")))
    (batch-read-items [this ids] (throw (RuntimeException. "NOT IMPLEMENTED")))))
