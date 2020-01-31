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

(defn delete-amr [id]
  (db/delete! amr-db id))

(defn write-amr [{id :id :as amr}]
  (when (get-amr id)
    (delete-amr id))
  (db/write! amr-db id amr))

(defn grammar-package []
  (io/file (or (System/getenv "GRAMMAR_PACKAGE") "grammar/concept-net.yaml")))

(defn read-amr [f]
  (let [{:keys [roles frames]} (utils/read-yaml f)]
    {:id     (utils/get-name f)
     :roles  (map #(cond
                     (string? %) {:type %}
                     (map? %) (select-keys % [:type :input :label])) roles)
     :frames (map (fn [{:keys [syntax example]}]
                    {:examples [example]
                     :syntax   (for [instance syntax]
                                 (reduce-kv (fn [m k v]
                                              (assoc m k (cond->> v
                                                                  (contains? #{:pos :type} k) (keyword)
                                                                  (= :params k) (map #(select-keys % [:role :type])))))
                                            {}
                                            (into {} instance)))})
                  frames)}))

(defn list-amr-files
  ([] (list-amr-files (grammar-package)))
  ([package]
   (let [parent (.getParent (io/file package))]
     (->> package
          (utils/read-yaml)
          (:includes)
          (map (partial io/file parent))))))

(defn initialize []
  (doseq [{id :id :as amr} (map read-amr (list-amr-files))]
    (when-not (get-amr id) (write-amr amr))))
