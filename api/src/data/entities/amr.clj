(ns data.entities.amr
  (:require [clojure.java.io :as io]
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

(defn list-amr-files []
  (->> (or (System/getenv "GRAMMAR_PATH") "resources/grammar")
       (io/file)
       (file-seq)
       (filter #(= ".yaml" (utils/get-ext %)))))

(defn load-single [id]
  (when-let [f (some #(when (= (name id) (utils/get-name %)) %) (list-amr-files))]
    (read-amr f)))

(defn load-all []
  (map read-amr (list-amr-files)))
