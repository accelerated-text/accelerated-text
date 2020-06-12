(ns data.datomic.entities.reader-flag
  (:require [data.datomic.utils :refer [remove-nil-vals]]
            [data.utils :refer [gen-uuid]]
            [datomic.api :as d]))

(defn prepare-reader-flag [flag value]
  {:reader-flag/id    (gen-uuid)
   :reader-flag/name  flag
   :reader-flag/value value})

(defn prepare-reader-flags [flags]
  (for [[flag value] flags]
    (prepare-reader-flag flag value)))

(defn transact-item [conn key value]
  (try
    @(d/transact conn [(remove-nil-vals
                       (dissoc (prepare-reader-flag key value) :db/id))])
    (catch Exception e (.printStackTrace e))))

(defn restore-reader-flags [flags]
  (into {} (for [{:reader-flag/keys [name value]} flags]
             [name value])))

(defn pull-entity [conn key]
  (:reader-flag/value (ffirst (d/q '[:find (pull ?e [*])
                                     :in $ ?key
                                     :where [?e :reader-flag/name ?key]]
                                   (d/db conn)
                                   key))))

(defn pull-n [conn limit]
  (restore-reader-flags
    (take limit (map first (d/q '[:find (pull ?e [*])
                                  :where [?e :reader-flag/value]]
                                (d/db conn))))))
