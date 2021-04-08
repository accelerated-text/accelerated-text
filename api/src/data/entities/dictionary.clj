(ns data.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [api.config :refer [conf]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [data.db :as db]
            [data.utils :as utils]
            [mount.core :refer [defstate]])
  (:import (java.io PushbackReader)))

(defn gen-id [{::dict-item/keys [key category sense language]}]
  (str/join "_" (remove nil? [key category sense language])))

(defstate dictionary-db :start (db/db-access :dictionary conf))

(defn list-dictionary-items
  ([] (list-dictionary-items Integer/MAX_VALUE))
  ([limit]
   (db/list! dictionary-db limit)))

(defn get-parent [{id ::dict-item-form/id}]
  (some (fn [{forms ::dict-item/forms :as dict-item}]
          (when (contains? (set (map ::dict-item-form/id forms)) id)
            dict-item))
        (list-dictionary-items)))

(defn get-dictionary-item [id]
  (db/read! dictionary-db id))

(defn delete-dictionary-item [id]
  (db/delete! dictionary-db id))

(defn update-dictionary-item [{id ::dict-item/id :as item}]
  (db/update! dictionary-db id item))

(defn update-dictionary-item-form [{id ::dict-item-form/id :as form}]
  (db/update! dictionary-db id form))

(defn scan-dictionary
  ([keys]
   (db/scan! dictionary-db {:keys keys}))
  ([keys languages]
   (db/scan! dictionary-db {:keys keys :languages languages}))
  ([keys languages categories]
   (db/scan! dictionary-db {:keys keys :languages languages :categories categories})))

(defn create-dictionary-item [{id ::dict-item/id :as item}]
  (let [item-id (or id (gen-id item))]
    (when (some? (get-dictionary-item item-id))
      (delete-dictionary-item item-id))
    (db/write! dictionary-db item-id item)
    (get-dictionary-item item-id)))

(defn get-dictionary-item-category [id]
  (::dict-item/category (get-dictionary-item id)))

(defn build-dictionaries [dict-keys language-codes]
  (let [dictionaries (group-by ::dict-item/language (scan-dictionary dict-keys language-codes))]
    (zipmap (keys dictionaries) (map set (vals dictionaries)))))

(defn read-dictionary-items-from-file [f]
  (with-open [r (io/reader f)]
    (doall
      (for [dict-item (edn/read (PushbackReader. r))]
        (-> (utils/add-ns-to-map "acc-text.nlg.dictionary.item" dict-item)
            (update ::dict-item/language #(cond->> % (nil? %) (get conf :default-language)))
            (update ::dict-item/forms (fn [forms]
                                        (map #(utils/add-ns-to-map
                                                "acc-text.nlg.dictionary.item.form"
                                                {:id (utils/gen-uuid) :value % :default? true})
                                             forms)))
            (update ::dict-item/attributes (fn [attrs]
                                             (map (fn [[name value]]
                                                    (utils/add-ns-to-map
                                                      "acc-text.nlg.dictionary.item.attr"
                                                      {:id (utils/gen-uuid) :name name :value value}))
                                                  attrs))))))))

(defstate dictionary
  :start (doseq [f (utils/list-files (:dictionary-path conf) #{".edn"})
                 dict-item (read-dictionary-items-from-file f)]
           (let [id (or (::dict-item/id dict-item) (gen-id dict-item))]
             (when-not (some? (get-dictionary-item id))
               (create-dictionary-item (assoc dict-item ::dict-item/id id)))))
  :stop (doseq [{id ::dict-item/id} (list-dictionary-items Integer/MAX_VALUE)]
          (delete-dictionary-item id)))
