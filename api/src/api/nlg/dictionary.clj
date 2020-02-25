(ns api.nlg.dictionary
  (:require [clojure.tools.logging :as log]
            [data.entities.dictionary :as dict-entity]))

(defn get-phrases [id]
  (:phrases (dict-entity/get-dictionary-item id)))

(defn use-phrase? [{{default-flags :default :as flags} :flags text :text} reader-profile]
  (let [other (->> (keys flags) (filter #(get reader-profile %)) (select-keys flags) (vals))]
    (log/tracef "Item: %s, logical table: %s default: %s" text (pr-str other) default-flags)
    (cond
      (some #(= % :NO) other) false
      (and (every? #(= % :DONT_CARE) other) (= default-flags :NO)) false
      :else true)))

(defn filter-by-profile [phrases reader-profile]
  (->> phrases
       (filter #(use-phrase? % reader-profile))
       (map :text)))

(defn search [key reader-profile]
  (filter-by-profile (get-phrases key) reader-profile))

(defn get-dict-item-by-language [key]
  (->> (dict-entity/search-multilang-dict key [:basic])
       (map #({(:language %) %}))
       (into {})))
