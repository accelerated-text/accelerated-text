(ns data.datomic.utils
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [io.rkn.conformity :as c]
            [datomic.api :as d])
  (:import (java.io File)
           (java.util UUID)))

(def schema-folder-name "datomic-schema")

(defn migrate [conn]
  (doseq [file-name (->> (file-seq (io/file (io/resource schema-folder-name)))
                         (remove #(.isDirectory ^File %))
                         (map #(.getName ^File %))
                         (sort))]
    (log/infof "Applying Datomic migration: %s" file-name)
    (c/ensure-conforms conn (c/read-resource (str schema-folder-name "/" file-name)))))

(defn init-db [uri]
  (d/create-database uri)
  uri)

(defn get-conn [conf]
  (let [c (d/connect (if-let [uri (:db-uri conf)]
                       (init-db uri)
                       (let [uri (str "datomic:mem://" (str (UUID/randomUUID)))]
                         (init-db uri))))]
    (migrate c)
    c))

(defn remove-nil-vals [m] (into {} (remove (comp nil? second) m)))
