(ns data.entities.amr
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [data.utils :as utils]
            [data.entities.dictionary :as dictionary]))

(defn read-amr [f]
  (let [{:keys [roles frames]} (utils/read-yaml f)]
    {:id                 (utils/get-name f)
     :dictionary-item-id (utils/get-name f)
     :thematic-roles     (map (fn [role] {:type role}) roles)
     :frames             (map (fn [{:keys [syntax example]}]
                                {:examples [example]
                                 :syntax   (for [instance syntax]
                                             (reduce-kv (fn [m k v]
                                                          (assoc m k (cond-> v
                                                                             (not (contains? #{:value :role} k))
                                                                             (keyword))))
                                                        {}
                                                        (into {} instance)))})
                              frames)}))

(defn list-package [package]
  (let [abs-path (.getParent (io/file package))]
    (->> package
         (utils/read-yaml)
         (:includes)
         (map (fn [p] (io/file (string/join "/" [abs-path p])))))))

(defn list-amr-files []
  (list-package (or (System/getenv "GRAMMAR_PACKAGE") "../grammar/all.yaml")))

(defn load-single [id]
  (when-let [f (some #(when (= (name id) (utils/get-name %)) %) (list-amr-files))]
    (read-amr f)))

(defn load-all [] (map read-amr (list-amr-files)))

(defn initialize []
  (doseq [f (list-amr-files)]
    (let [{:keys [members]} (utils/read-yaml f)
          amr-key (utils/get-name f)]
      (when-not (dictionary/get-dictionary-item amr-key)
        (dictionary/create-dictionary-item
          {:key     amr-key
           :name    amr-key
           :phrases members})))))
