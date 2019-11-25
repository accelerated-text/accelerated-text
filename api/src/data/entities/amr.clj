(ns data.entities.amr
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [data.utils :as utils]))

(defn read-amr [f]
  (let [{:keys [members roles frames]} (utils/read-yaml f)]
    {:id                 (utils/get-name f)
     :dictionary-item-id (first members)
     :thematic-roles     (map (fn [role] {:type role}) roles)
     :frames             (map (fn [{:keys [syntax example]}]
                                {:examples [example]
                                 :syntax   (for [instance syntax]
                                             (into {} (update instance :pos keyword)))})
                              frames)}))

(defn list-package [package]
  (let [abs-path (.getParent (io/file package))]
    (->> package
         (utils/read-yaml)
         :includes
         (map (fn [p] (string/join "/" [abs-path p])))
         (map io/file))))

(defn list-amr-files []
  (list-package (or (System/getenv "GRAMMAR_PACKAGE") "test/resources/grammar/default.yaml")))

(defn load-single [id]
  (when-let [f (some #(when (= (name id) (utils/get-name %)) %) (list-amr-files))]
    (read-amr f)))

(defn load-all []
  (map read-amr (list-amr-files)))
