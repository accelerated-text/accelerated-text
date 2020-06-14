(ns data.datomic.impl
  (:require [api.config :refer [conf]]
            [clojure.tools.logging :as log]
            [data.datomic.entities.data-files :as data-files]
            [data.datomic.entities.dictionary :as dictionary]
            [data.datomic.entities.document-plan :as document-plan]
            [data.datomic.entities.language :as language]
            [data.datomic.entities.results :as results]
            [data.protocol :as protocol]
            [mount.core :refer [defstate]]
            [data.datomic.utils :as utils]))

(defstate conn :start (utils/get-conn conf))

(defmulti transact-item (fn [resource-type _ _] resource-type))
(defmulti pull-entity (fn [resource-type _] resource-type))
(defmulti pull-n (fn [resource-type _] resource-type))
(defmulti scan (fn [resource-type _] resource-type))
(defmulti delete (fn [resource-type _] resource-type))
(defmulti update! (fn [resource-type _ _] resource-type))

(defmethod transact-item :default [resource-type key _]
  (log/warnf "Default implementation of transact-item for the '%s' with key '%s'" resource-type key)
  (throw (RuntimeException. (format "DATOMIC TRANSACT-ITEM FOR '%s' NOT IMPLEMENTED" resource-type))))
(defmethod pull-entity :default [resource-type key]
  (log/warnf "Default implementation of pull-entity for the '%s' with key '%s'" resource-type key)
  (throw (RuntimeException. (format "DATOMIC PULL-ENTITY FOR '%s' NOT IMPLEMENTED" resource-type))))
(defmethod pull-n :default [resource-type limit]
  (log/warnf "Default implementation of list-items for the '%s' with key '%s'" resource-type limit)
  (throw (RuntimeException. (format "DATOMIC PULL-N FOR '%s' NOT IMPLEMENTED" resource-type))))
(defmethod scan :default [resource-type opts]
  (log/warnf "Default implementation of SCAN for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC SCAN FOR '%s' NOT IMPLEMENTED" resource-type))))
(defmethod delete :default [resource-type opts]
  (log/warnf "Default implementation of DELETE for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC DELETE FOR '%s' NOT IMPLEMENTED" resource-type))))
(defmethod update! :default [resource-type key data]
  (log/errorf "Default UPDATE for %s with key %s and %s" resource-type key data)
  (throw (RuntimeException. (format "DATOMIC UPDATE FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmethod transact-item :data-files [_ key data-item]
  (data-files/transact-item conn key data-item))
(defmethod pull-entity :data-files [_ key]
  (data-files/pull-entity conn key))
(defmethod pull-n :data-files [_ limit]
  (data-files/pull-n conn limit))

(defmethod transact-item :dictionary [_ key data-item]
  (dictionary/transact-item conn key data-item))
(defmethod pull-entity :dictionary [_ key]
  (dictionary/pull-entity conn key))
(defmethod pull-n :dictionary [_ limit]
  (dictionary/pull-n conn limit))
(defmethod scan :dictionary [_ opts]
  (dictionary/scan conn opts))
(defmethod update! :dictionary [resource-type key data-item]
  (dictionary/update! conn resource-type key data-item))
(defmethod delete :dictionary [_ key]
  (dictionary/delete conn key))

(defmethod delete :document-plan [_ key]
  (document-plan/delete conn key))
(defmethod pull-entity :document-plan [_ key]
  (document-plan/pull-entity conn key))
(defmethod update! :document-plan [_ key data-item]
  (document-plan/update! conn key data-item))
(defmethod scan :document-plan [_ _]
  (document-plan/scan conn))
(defmethod transact-item :document-plan [_ key data-item]
  (document-plan/transact-item conn key data-item))

(defmethod transact-item :language [_ _ data-item]
  (language/transact-item conn data-item))
(defmethod pull-entity :language [_ code]
  (language/pull-entity conn code))
(defmethod pull-n :language [_ limit]
  (language/pull-n conn limit))

(defmethod transact-item :results [_ _ data-item]
  (results/transact-item conn data-item))
(defmethod pull-entity :results [_ key]
  (results/pull-entity conn key))

(defn db-access
  [resource-type config]
  (log/debugf "Datomic for: %s with config %s" resource-type config)
  (reify
    protocol/DBAccess
    (read-item [this key]
      (pull-entity resource-type key))
    (write-item [this key data update-count?]
      (transact-item resource-type key data))
    (update-item [this key data]
      (try
        (update! resource-type key data)
        (catch Exception e
          (.printStackTrace e))))
    (delete-item [this key] (delete resource-type key))
    (list-items [this limit] (pull-n resource-type limit))
    (scan-items [this opts] (scan resource-type opts))
    (batch-read-items [this ids]
      (throw (RuntimeException. (format "DATOMIC BATCH-READ-ITEMS FOR '%s' NOT IMPLEMENTED" resource-type))))))
