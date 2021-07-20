(ns data.entities.reader-model
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [data.db :as db]
            [data.entities.user-group :as user-group]
            [data.spec.reader-model :as reader-model]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate reader-model-db :start (db/db-access :reader-model conf))

(defn drop-group-id [reader]
  (when (some? reader)
    (update reader ::reader-model/code #(last (re-find #"(.+?)#(.+)" %)))))

(defn fetch [code group-id]
  (drop-group-id (db/read! reader-model-db (str group-id "#" code))))

(defn update! [{code ::reader-model/code :as reader} group-id]
  (db/write! reader-model-db (assoc reader ::reader-model/code (str group-id "#" code)))
  (user-group/link-reader-model group-id (str group-id "#" code))
  (fetch code group-id))

(defn delete! [{code ::reader-model/code} group-id]
  (db/delete! reader-model-db (str group-id "#" code)))

(defn list-reader-model
  ([group-id] (list-reader-model group-id 100))
  ([group-id limit]
   (map drop-group-id (take limit (-> (user-group/get-or-create-group group-id) ::user-group/reader-models)))))

(defn available-readers [group-id]
  (filter (fn [{::reader-model/keys [type available?]}]
            (and (= type :reader) (true? available?)))
          (list-reader-model group-id)))

(defn available-languages [group-id]
  (filter (fn [{::reader-model/keys [type available?]}]
            (and (= type :language) (true? available?)))
          (list-reader-model group-id)))

(defn available-reader-model [group-id]
  (filter #(true? (::reader-model/available? %)) (list-reader-model group-id)))

(defn reader-model-config-path []
  (io/file (get conf :config-path (io/resource "config")) "readers.edn"))

(defn language-config-path []
  (io/file (get conf :config-path (io/resource "config")) "languages.edn"))

(defstate reader-conf :start
  (do
    (doseq [reader (available-readers user-group/DUMMY-USER-GROUP-ID)] (delete! reader user-group/DUMMY-USER-GROUP-ID))
    (->> (reader-model-config-path)
         (utils/read-edn)
         (filter ::reader-model/available?)
         (mapv #(update! (assoc % ::reader-model/type :reader
                                  ::reader-model/enabled? (contains? (:enabled-readers conf)
                                                                     (str/capitalize (::reader-model/code %))))
                         user-group/DUMMY-USER-GROUP-ID)))))

(defstate language-conf :start
  (do
    (doseq [lang (available-languages user-group/DUMMY-USER-GROUP-ID)] (delete! lang user-group/DUMMY-USER-GROUP-ID))
    (->> (language-config-path)
         (utils/read-edn)
         (filter ::reader-model/available?)
         (mapv #(update! (assoc % ::reader-model/type :language
                                  ::reader-model/enabled? (contains? (:enabled-languages conf)
                                                                     (str/capitalize (::reader-model/code %))))
                         user-group/DUMMY-USER-GROUP-ID)))))
