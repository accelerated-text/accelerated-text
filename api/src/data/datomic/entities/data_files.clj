(ns data.datomic.entities.data-files
  (:require [datomic.api :as d]))

(defn transact-item [conn key data-item]
  @(d/transact conn [{:data-file/id       key
                      :data-file/filename (:filename data-item)
                      :data-file/content  (:content data-item)}]))

(defn pull-entity [conn key]
  (let [data-file (ffirst (d/q '[:find (pull ?e [*])
                                 :in $ ?key
                                 :where
                                 [?e :data-file/id ?key]]
                               (d/db conn)
                               key))]
    (when data-file
      {:id       (:data-file/id data-file)
       :filename (:data-file/filename data-file)
       :content  (:data-file/content data-file)})))

(defn pull-n [conn limit]
  (let [resp (map first (d/q '[:find (pull ?e [*])
                               :where [?e :data-file/id]]
                             (d/db conn)))]

    (map (fn [df] {:id       (:data-file/id df)
                   :filename (:data-file/filename df)
                   :content  (:data-file/content df)}) (take limit resp))))
