(ns api.datomic-fixtures
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [datomic.api :as d]
            [io.rkn.conformity :as c]
            [mount.core :as mount])
  (:import (java.util UUID)
           (java.io File)))

(defn scratch-conn
  "Creates an in-memory Datomic connection.
  NOTE: we actually won't be using this implementation, see next section on forking connections."
  ([] (scratch-conn (str "mem-conn-" (UUID/randomUUID))))
  ([db-name]
   (let [uri (str "datomic:mem://" db-name)]
     (d/create-database uri)
     (d/connect uri))))

(def schema-folder-name "datomic-schema")

(defn migrate [conn]
  (doseq [file-name (->> (file-seq (io/file (io/resource schema-folder-name)))
                         (remove #(.isDirectory ^File %))
                         (map #(.getName ^File %))
                         (sort))]
    (log/infof "Applying Datomic migration: %s" file-name)
    (c/ensure-conforms conn (c/read-resource (str schema-folder-name "/" file-name)))))

(def data-file {:data-file/id "id" :data-file/filename "filename" :data-file/content "content"})

(defn datomix-fixture [f]
  (let [db-name (str (UUID/randomUUID))
        conn (scratch-conn db-name)
        _ (migrate conn)]
    (-> (mount/swap-states
          {#'api.config/conf
           {:start (fn []
                     {:db-implementation  :datomic
                      :db-name            db-name
                      :db-uri             (str "datomic:mem://" db-name)
                      :validate-hostnames false})}})
        (mount/only #{#'api.config/conf
                      #'data.entities.data-files/data-files-db
                      #'data.datomic.impl/conn})
        (mount/start)))
  (f))
