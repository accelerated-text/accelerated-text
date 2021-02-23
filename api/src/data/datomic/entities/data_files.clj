(ns data.datomic.entities.data-files
  (:require [data.spec.data-file :as data-file]
            [datomic.api :as d]))

(def pattern [:data-file/id
              :data-file/filename
              :data-file/content])

(defn translate [{:data-file/keys [id filename content] :as data-file}]
  (when (some? data-file)
    #::data-file{:id      id
                 :name    filename
                 :content content}))

(defn transact-item [conn key data-item]
  @(d/transact conn [#:data-file{:id       key
                                 :filename (:filename data-item)
                                 :content  (:content data-item)}]))

(defn pull-entity [conn key]
  (translate (d/pull (d/db conn) pattern [:data-file/id key])))

(defn pull-n [conn limit]
  (map (comp translate first)
       (take limit (d/q '[:find (pull ?e pattern)
                          :in $ pattern
                          :where
                          [?e :data-file/id]]
                        (d/db conn) pattern))))

(defn delete [conn key]
  @(d/transact conn [[:db.fn/retractEntity [:data-file/id key]]]))
