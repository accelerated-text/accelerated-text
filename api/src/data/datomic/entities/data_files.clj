(ns data.datomic.entities.data-files
  (:require [data.spec.data-file :as data-file]
            [datomic.api :as d]))

(def pattern [::data-file/id
              ::data-file/name
              ::data-file/timestamp
              ::data-file/content])

(defn transact-item [conn key data-item]
  @(d/transact conn [(assoc data-item ::data-file/id key)]))

(defn pull-entity [conn key]
  (d/pull (d/db conn) pattern [::data-file/id key]))

(defn pull-n [conn limit]
  (map first (take limit (d/q '[:find (pull ?e pattern)
                                :in $ pattern
                                :where
                                [?e ::data-file/id]]
                              (d/db conn) pattern))))

(defn delete [conn key]
  @(d/transact conn [[:db.fn/retractEntity [::data-file/id key]]]))
