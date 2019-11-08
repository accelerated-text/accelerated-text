(ns data.entities.amr
  (:require [clojure.java.io :as io]
            [data.utils :as utils]))

(defn parse-amr [{:keys [id members roles frames]}]
  {:id                 id
   :dictionary-item-id (first members)
   :thematic-roles     (map (fn [role] {:type role}) roles)
   :frames             (map (fn [{:keys [syntax example]}]
                              {:examples [example]
                               :syntax   (for [instance syntax]
                                           (into {} (update instance :pos keyword)))})
                            frames)})

(defn rules []
  (reduce (fn [m f]
            (if-not (= ".yaml" (utils/get-ext f))
              m
              (let [{id :id :as amr} (-> f (utils/read-yaml) (parse-amr))]
                (assoc m id amr))))
          {}
          (-> "amr" (io/resource) (io/file) (file-seq))))

(defn list-verbclasses [] (map (fn [[_ v]] v) (rules)))

(defn get-verbclass [k] (get (rules) (keyword k)))
