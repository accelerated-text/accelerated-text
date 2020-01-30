(ns data.entities.amr
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]]))

(defstate amr-db :start (db/db-access :amr conf))

(defn list-amrs []
  (db/list! amr-db 100))

(defn get-amr [id]
  (when-not (str/blank? id)
    (db/read! amr-db id)))

(defn write-amr [{id :id :as amr}]
  (when (some? id)
    (db/write! amr-db id amr)))

(defn delete-amr [id]
  (db/delete! amr-db id))

(defn update-amr [{id :id :as amr}]
  (db/update! amr-db id (dissoc amr :id)))

(defn read-amr [f]
  (let [{:keys [roles frames]} (utils/read-yaml f)]
    {:id     (utils/get-name f)
     :roles  (map (fn [role] {:type role}) roles)
     :frames (map (fn [{:keys [syntax example]}]
                    {:examples [example]
                     :syntax   (for [instance syntax]
                                 (reduce-kv (fn [m k v]
                                              (assoc m k (cond-> v
                                                                 (not (contains? #{:value :role :roles :ret} k))
                                                                 (keyword))))
                                            {}
                                            (into {} instance)))})
                  frames)}))

(defn list-package [package]
  (let [abs-path (.getParent (io/file package))]
    (->> package
         (utils/read-yaml)
         (:includes)
         (map (fn [p] (io/file (str/join "/" [abs-path p])))))))

(defn list-amr-files []
  (list-package (or (System/getenv "GRAMMAR_PACKAGE") "../grammar/concept-net.yaml")))

(defn initialize []
  (doseq [{id :id :as amr} (map read-amr (list-amr-files))]
    (when-not (get-amr id) (write-amr amr))))
