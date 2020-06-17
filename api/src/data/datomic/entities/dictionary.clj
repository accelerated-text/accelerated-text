(ns data.datomic.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.attr :as dict-item-attr]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [clojure.tools.logging :as log]
            [datomic.api :as d]))

(def pattern [::dict-item/id
              ::dict-item/key
              ::dict-item/category
              ::dict-item/sense
              ::dict-item/definition
              ::dict-item/language
              {::dict-item/forms [::dict-item-form/id
                                  ::dict-item-form/value
                                  ::dict-item-form/default?]}
              {::dict-item/attributes [::dict-item-attr/id
                                       ::dict-item-attr/name
                                       ::dict-item-attr/value]}])

(defn transact-item [conn key data-item]
  (try
    @(d/transact conn [(assoc data-item ::dict-item/id key)])
    (catch Exception e (.printStackTrace e))))

(defn pull-entity [conn key]
  (d/pull (d/db conn) pattern [::dict-item/id key]))

(defn pull-n [conn limit]
  (map first (take limit (d/q '[:find (pull ?e pattern)
                                :in $ pattern
                                :where
                                [?e ::dict-item/id]]
                              (d/db conn) pattern))))

(defn query-dictionary [conn keys languages categories]
  (cond
    (and (seq languages) (seq categories)) (d/q '[:find (pull ?e pattern)
                                                  :in $ ?keys ?languages ?categories pattern
                                                  :where [?e ::dict-item/key ?key]
                                                  [?e ::dict-item/language ?language]
                                                  [?e ::dict-item/category ?category]
                                                  [(contains? ?categories ?category)]
                                                  [(contains? ?languages ?language)]
                                                  [(contains? ?keys ?key)]]
                                                (d/db conn) (set keys) (set languages) (set categories) pattern)
    (seq languages) (d/q '[:find (pull ?e pattern)
                           :in $ ?keys ?languages pattern
                           :where [?e ::dict-item/key ?key]
                           [?e ::dict-item/language ?language]
                           [(contains? ?languages ?language)]
                           [(contains? ?keys ?key)]]
                         (d/db conn) (set keys) (set languages) pattern)
    :else (d/q '[:find (pull ?e pattern)
                 :in $ ?keys pattern
                 :where [?e ::dict-item/key ?key]
                 [(contains? ?keys ?key)]]
               (d/db conn) (set keys) pattern)))

(defn scan [conn {:keys [keys languages categories]}]
  (map first (query-dictionary conn keys languages categories)))

(defn update! [conn key data-item]
  (try
    @(d/transact conn [[:db.fn/retractEntity [::dict-item/id key]]])
    @(d/transact conn [data-item])
    (catch Exception e
      (log/errorf "Error %s with data %s" e val)))
  (pull-entity conn key))

(defn delete [conn key]
  @(d/transact conn [[:db.fn/retractEntity [::dict-item/id key]]])
  nil)
