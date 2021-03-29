(ns data.datomic.entities.user-group
    (:require [data.spec.user-group :as user-group]
              [datomic.api :as d]
              [data.datomic.utils :refer [remove-nil-vals]]))

(def pattern [::user-group/id
              ::user-group/data-files
              ::user-group/document-plans])

(defn pull-entity [conn key]
  (d/pull (d/db conn) pattern [::user-group/id key]))

(defn update! [conn key data]
  (println data)
  @(d/transact conn [(remove-nil-vals
                      {:db/id                      [::user-group/id key]
                       ::user-group/data-files     (::user-group/data-files data)
                       ::user-group/document-plans (::user-group/document-plans data)})])
  (pull-entity conn key))

(defn transact-item [conn key data]
  @(d/transact conn [(remove-nil-vals
                      {::user-group/id             key
                       ::user-group/data-files     (::user-group/data-files data)
                       ::user-group/document-plans (::user-group/document-plans data)})])
  (assoc data :id key))