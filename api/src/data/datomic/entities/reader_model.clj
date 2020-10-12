(ns data.datomic.entities.reader-model
  (:require [data.spec.reader-model :as reader-model]
            [datomic.api :as d]))

(def pattern [::reader-model/code
              ::reader-model/name
              ::reader-model/type
              ::reader-model/flag
              ::reader-model/available?
              ::reader-model/enabled?])

(defn transact-item [conn data-item]
  (try
    @(d/transact conn [data-item])
    (catch Exception e (.printStackTrace e))))

(defn pull-entity [conn code]
  (d/pull (d/db conn) pattern [::reader-model/code code]))

(defn pull-n [conn limit]
  (->> (d/q '[:find (pull ?e pattern)
              :in $ pattern
              :where [?e ::reader-model/code]]
            (d/db conn)
            pattern)
       (map first)
       (take limit)))
