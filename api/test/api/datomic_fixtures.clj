(ns api.datomic-fixtures
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [datomic.api :as d]
            [io.rkn.conformity :as c])
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
  (let [conn (scratch-conn)]
    (migrate conn)
    (log/spy (d/transact conn [data-file]))
    (f)))
