(ns data.datomic.entities.dictionary
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.item.attr :as dict-item-attr]
            [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [clojure.tools.logging :as log]
            [datomic.api :as d]))

(defn schema->dict-item [{::dict-item/keys [forms attributes] :as dict-item}]
  (cond-> (dissoc dict-item :db/id)
          (seq forms) (update ::dict-item/forms #(map ::dict-item-form/value %))
          (seq attributes) (update ::dict-item/attributes #(reduce (fn [m {::dict-item-attr/keys [name value]}]
                                                                     (assoc m name value))
                                                                   {}
                                                                   %))))

(defn dict-item->schema [{::dict-item/keys [forms attributes] :as dict-item}]
  (cond-> dict-item
          (seq forms) (update ::dict-item/forms #(map (fn [form] {::dict-item-form/value form}) %))
          (seq attributes) (update ::dict-item/attributes #(reduce-kv (fn [acc k v]
                                                                        (conj acc #::dict-item-attr{:name k :value v}))
                                                                      []
                                                                      %))))

(defn transact-item [conn key data-item]
  (try
    @(d/transact conn [(log/spy (dict-item->schema (assoc data-item ::dict-item/id key)))])
    (catch Exception e (.printStackTrace e))))

(defn pull-entity [conn key]
  (schema->dict-item
    (d/pull
      (d/db conn)
      [::dict-item/id
       ::dict-item/key
       ::dict-item/category
       ::dict-item/sense
       ::dict-item/definition
       ::dict-item/language
       {::dict-item/forms [::dict-item-form/value]}
       {::dict-item/attributes [::dict-item-attr/name
                                ::dict-item-attr/value]}]
      [::dict-item/id key])))

(defn pull-n [conn limit]
  (map (comp schema->dict-item first)
       (take limit (d/q '[:find (pull ?e [::dict-item/id
                                          ::dict-item/key
                                          ::dict-item/category
                                          ::dict-item/sense
                                          ::dict-item/definition
                                          ::dict-item/language
                                          {::dict-item/forms [::dict-item-form/value]}
                                          {::dict-item/attributes [::dict-item-attr/name
                                                                   ::dict-item-attr/value]}])
                          :where
                          [?e ::dict-item/id]]
                        (d/db conn)))))

(defn query-dictionary [conn keys languages categories]
  (cond
    (and (seq languages) (seq categories)) (d/q '[:find (pull ?e [*])
                                                  :in $ [?keys ?languages ?categories]
                                                  :where [?e ::dict-item/key ?key]
                                                  [?e ::dict-item/language ?language]
                                                  [?e ::dict-item/category ?category]
                                                  [(contains? ?categories ?category)]
                                                  [(contains? ?languages ?language)]
                                                  [(contains? ?keys ?key)]]
                                                (d/db conn)
                                                [(set keys) (set languages) (set categories)])
    (seq languages) (d/q '[:find (pull ?e [*])
                           :in $ [?keys ?languages]
                           :where [?e ::dict-item/key ?key]
                           [?e ::dict-item/language ?language]
                           [(contains? ?languages ?language)]
                           [(contains? ?keys ?key)]]
                         (d/db conn)
                         [(set keys) (set languages)])
    :else (d/q '[:find (pull ?e [*])
                 :in $ ?keys
                 :where [?e ::dict-item/key ?key]
                 [(contains? ?keys ?key)]]
               (d/db conn)
               (set keys))))

(defn scan [conn {:keys [keys languages categories]}]
  (map (comp schema->dict-item first) (query-dictionary conn keys languages categories)))

(defn update! [conn resource-type key data-item]
  (try
    @(d/transact conn [[:db.fn/retractEntity [::dict-item/id key]]])
    @(d/transact conn [(dict-item->schema data-item)])
    (catch Exception e
      (log/errorf "Error %s with data %s" e val)))
  (pull-entity resource-type key))

(defn delete [conn key]
  @(d/transact conn [[:db.fn/retractEntity [::dict-item/id key]]])
  nil)
