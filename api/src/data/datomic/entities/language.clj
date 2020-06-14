(ns data.datomic.entities.language
  (:require [data.spec.language :as language]
            [datomic.api :as d]))

(defn transact-item [conn data-item]
  (try
    @(d/transact conn (cond-> data-item (map? data-item) (vector)))
    (catch Exception e (.printStackTrace e))))

(defn pull-entity [conn code]
  (d/pull (d/db conn)
          [::language/code
           ::language/name
           ::language/enabled?]
          [::language/code code]))

(defn pull-n [conn limit]
  (->> (d/db conn)
       (d/q '[:find (pull ?e [::language/code
                              ::language/name
                              ::language/enabled?])
              :where [?e ::language/code]])
       (map first)
       (take limit)))
