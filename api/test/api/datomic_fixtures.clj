(ns api.datomic-fixtures
  (:require [api.config]
            [data.datomic.impl]
            [data.entities.amr]
            [data.entities.data-files]
            [data.entities.dictionary]
            [data.entities.document-plan]
            [data.entities.rgl]
            [data.entities.result]
            [datomic.api :as d]
            [mount.core :as mount])
  (:import (java.util UUID)))

(defn scratch-conn
  "Creates an in-memory Datomic connection.
  NOTE: we actually won't be using this implementation, see next section on forking connections."
  ([] (scratch-conn (str "mem-conn-" (UUID/randomUUID))))
  ([db-name]
   (let [uri (str "datomic:mem://" db-name)]
     (d/create-database uri))))

(defn datomix-fixture [f]
  (let [db-name (str (UUID/randomUUID))]
    (scratch-conn db-name)
    (mount/stop)
    (-> (mount/swap-states
          {#'api.config/conf
           {:start (fn []
                     {:db-implementation :datomic})}})
        (mount/only #{#'api.config/conf
                      #'data.entities.rgl/rgl-db
                      #'data.entities.data-files/data-files-db
                      #'data.entities.document-plan/document-plans-db
                      #'data.entities.dictionary/reader-flags-db
                      #'data.entities.dictionary/dictionary-db
                      #'data.entities.result/results-db
                      #'data.datomic.impl/conn})
        (mount/start)))
  (f))
