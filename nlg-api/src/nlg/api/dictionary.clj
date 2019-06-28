(ns nlg.api.dictionary
  (:require [data-access.entities.dictionary :as dict-entity]
            [clojure.tools.logging :as log]))

(defn get-phrases
  [id]
  (-> (dict-entity/get-dictionary-item id)
      :phrases))

(defn use-phrase?
  [phrase reader-profile]
  (let [flags (:flags phrase)
        default (:default flags)
        profile-keys (filter #(get reader-profile %) (keys flags))
        other (vals (select-keys flags profile-keys))]
    (log/tracef "Item: %s, logical table: %s default: %s" (:text phrase) (pr-str other) default)
    (cond
      (some #(= % :NO) other) false
      (and (every? #(= % :DONT_CARE) other) (= default :NO)) false
      :else true)))

(defn filter-by-profile
  [phrases reader-profile]
  (map
   :text
   (filter #(use-phrase? % reader-profile) phrases)))

(defn search
  [key reader-profile]
  (filter-by-profile (get-phrases key) reader-profile))
