(ns data.datomic.impl
  (:require [data.spec.result :as result]
            [data.spec.result.row :as result-row]
            [data.spec.result.annotation :as result-annotation]
            [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.config :refer [conf]]
            [clojure.tools.logging :as log]
            [data.protocol :as protocol]
            [datomic.api :as d]
            [mount.core :refer [defstate]]
            [data.datomic.utils :as utils :refer [remove-nil-vals]]
            [data.utils :refer [ts-now gen-uuid]]
            [data.datomic.blockly :as blockly]
            [jsonista.core :as json]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn encode-results [results]
  (map json/write-value-as-string results))

(defn decode-results [results]
  (map #(json/read-value % read-mapper) results))

(defstate conn :start (utils/get-conn conf))

(defmulti transact-item (fn [resource-type _ _] resource-type))

(defmethod transact-item :data-files [_ key data-item]
  @(d/transact conn [{:data-file/id       key
                      :data-file/filename (:filename data-item)
                      :data-file/content  (:content data-item)}]))

(defn prepare-reader-flag [flag value]
  {:reader-flag/id    (gen-uuid)
   :reader-flag/name  flag
   :reader-flag/value value})

(defn prepare-reader-flags [flags]
  (for [[flag value] flags]
    (prepare-reader-flag flag value)))

(defn prepare-dictionary-item [key data-item]
  {:db/id                            [:dictionary-combined/id key]
   :dictionary-combined/id           key
   :dictionary-combined/name         (:name data-item)
   :dictionary-combined/partOfSpeech (:partOfSpeech data-item)
   :dictionary-combined/phrases      (->> (:phrases data-item)
                                          (map (fn [{:keys [id text flags]}]
                                                 (remove-nil-vals
                                                   {:phrase/id    id
                                                    :phrase/text  text
                                                    :phrase/flags (prepare-reader-flags flags)})))
                                          (remove empty?))})

(defmethod transact-item :dictionary-combined [_ key data-item]
  (try
    @(d/transact conn [(remove-nil-vals (dissoc (prepare-dictionary-item key data-item) :db/id))])
    (assoc data-item :key key)
    (catch Exception e (.printStackTrace e))))

(defmethod transact-item :results [_ _ data-item]
  @(d/transact conn [(assoc data-item ::result/timestamp (ts-now))]))

(defn prepare-multilang-dict [id {::dictionary-item/keys [key category language forms sense definition attributes]}]
  {:db/id                           [:dictionary-multilang/id id]
   :dictionary-multilang/id         id
   :dictionary-multilang/key        key
   :dictionary-multilang/category   category
   :dictionary-multilang/language   language
   :dictionary-multilang/sense      sense
   :dictionary-multilang/definition definition
   :dictionary-multilang/forms      (map (fn [form]
                                           {:form/id    (gen-uuid)
                                            :form/value form})
                                         forms)
   :dictionary-multilang/attributes (map (fn [[k v]]
                                           {:attribute/id    (gen-uuid)
                                            :attribute/key   k
                                            :attribute/value v})
                                         attributes)})

(defn read-multilang-dict-item [{:dictionary-multilang/keys [id key category language forms sense definition attributes]}]
  (remove-nil-vals
    {::dictionary-item/id         id
     ::dictionary-item/key        key
     ::dictionary-item/category   category
     ::dictionary-item/language   language
     ::dictionary-item/sense      sense
     ::dictionary-item/definition definition
     ::dictionary-item/forms      (mapv :form/value forms)
     ::dictionary-item/attributes (when (seq attributes)
                                    (reduce (fn [m {:attribute/keys [key value]}]
                                              (assoc m key value))
                                            {}
                                            attributes))}))

(defmethod transact-item :dictionary-multilang [_ key data-item]
  (try
    @(d/transact conn [(remove-nil-vals
                         (dissoc (prepare-multilang-dict key data-item) :db/id))])
    (catch Exception e (.printStackTrace e))))

(defmethod transact-item :reader-flag [_ key value]
  (try
    @(d/transact conn [(remove-nil-vals
                         (dissoc (prepare-reader-flag key value) :db/id))])
    (catch Exception e (.printStackTrace e))))

(defn prepare-rgl-syntax-params [params]
  (->> params
       (map (fn [{:keys [id type role]}]
              (remove-nil-vals
                {:param/id   id
                 :param/type type
                 :param/role role})))
       (remove empty?)))

(defn prepare-rgl-syntax [syntax]
  (->> syntax
       (map (fn [{:keys [role ret value params pos type]}]
              (remove-nil-vals
                {:syntax/role   role
                 :syntax/ret    ret
                 :syntax/value  value
                 :syntax/pos    pos
                 :syntax/type   type
                 :syntax/params (prepare-rgl-syntax-params params)})))
       (remove empty?)))

(defmethod transact-item :default [resource-type key _]
  (log/warnf "Default implementation of transact-item for the '%s' with key '%s'" resource-type key)
  (throw (RuntimeException. (format "DATOMIC TRANSACT-ITEM FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti pull-entity (fn [resource-type _] resource-type))

(defmethod pull-entity :data-files [_ key]
  (let [data-file (ffirst (d/q '[:find (pull ?e [*])
                                 :in $ ?key
                                 :where
                                 [?e :data-file/id ?key]]
                               (d/db conn)
                               key))]
    (when data-file
      {:id       (:data-file/id data-file)
       :filename (:data-file/filename data-file)
       :content  (:data-file/content data-file)})))

(defn restore-reader-flags [flags]
  (into {} (for [{:reader-flag/keys [name value]} flags]
             [name value])))

(defmethod pull-entity :reader-flag [_ key]
  (:reader-flag/value (ffirst (d/q '[:find (pull ?e [*])
                                     :in $ ?key
                                     :where [?e :reader-flag/name ?key]]
                                   (d/db conn)
                                   key))))

(defmethod pull-entity :dictionary-combined [_ key]
  (let [dictionary-entry (ffirst (d/q '[:find (pull ?e [*])
                                        :in $ ?key
                                        :where [?e :dictionary-combined/id ?key]]
                                      (d/db conn)
                                      key))]
    (when dictionary-entry
      {:key     (:dictionary-combined/id dictionary-entry)
       :name    (:dictionary-combined/name dictionary-entry)
       :phrases (map (fn [phrase] {:id    (:phrase/id phrase)
                                   :text  (:phrase/text phrase)
                                   :flags (restore-reader-flags (:phrase/flags phrase))})
                     (:dictionary-combined/phrases dictionary-entry))})))

(defmethod pull-entity :dictionary-multilang [_ key]
  (when-let [item (ffirst (d/q '[:find (pull ?e [*])
                                 :in $ ?key
                                 :where [?e :dictionary-multilang/id ?key]]
                               (d/db conn)
                               key))]
    (read-multilang-dict-item item)))

(defmethod pull-entity :results [_ key]
  (d/pull
    (d/db conn)
    [::result/id
     ::result/status
     ::result/error-message
     ::result/timestamp
     {::result/rows [::result-row/id
                     ::result-row/text
                     ::result-row/language
                     ::result-row/enriched?
                     {::result-row/annotations [::result-annotation/id
                                                ::result-annotation/idx
                                                ::result-annotation/text]}]}]
    [::result/id key]))

(defmethod pull-entity :default [resource-type key]
  (log/warnf "Default implementation of pull-entity for the '%s' with key '%s'" resource-type key)
  (throw (RuntimeException. (format "DATOMIC PULL-ENTITY FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti pull-n (fn [resource-type _] resource-type))

(defmethod pull-n :data-files [_ limit]
  (let [resp (map first (d/q '[:find (pull ?e [*])
                               :where [?e :data-file/id]]
                             (d/db conn)))]

    (map (fn [df] {:id       (:data-file/id df)
                   :filename (:data-file/filename df)
                   :content  (:data-file/content df)}) (take limit resp))))

(defmethod pull-n :reader-flag [_ limit]
  (restore-reader-flags
    (take limit (map first (d/q '[:find (pull ?e [*])
                                  :where [?e :reader-flag/value]]
                                (d/db conn))))))

(defmethod pull-n :dictionary-combined [_ limit]
  (take limit (map (fn [[item]]
                     {:key          (:dictionary-combined/id item)
                      :name         (:dictionary-combined/name item)
                      :partOfSpeech (:dictionary-combined/partOfSpeech item)
                      :phrases      (map (fn [phrase] {:id    (:phrase/id phrase)
                                                       :text  (:phrase/text phrase)
                                                       :flags (restore-reader-flags (:phrase/flags phrase))})
                                         (:dictionary-combined/phrases item))})
                   (d/q '[:find (pull ?e [*])
                          :where [?e :dictionary-combined/id]]
                        (d/db conn)))))


(defmethod pull-n :dictionary-multilang [_ limit]
  (take limit (map (fn [[item]] (read-multilang-dict-item item))
                   (d/q '[:find (pull ?e [*])
                          :where [?e :dictionary-multilang/id]]
                        (d/db conn)))))

(defmethod pull-n :default [resource-type limit]
  (log/warnf "Default implementation of list-items for the '%s' with key '%s'" resource-type limit)
  (throw (RuntimeException. (format "DATOMIC PULL-N FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti scan (fn [resource-type _] resource-type))

(defmethod scan :default [resource-type opts]
  (log/warnf "Default implementation of SCAN for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC SCAN FOR '%s' NOT IMPLEMENTED" resource-type))))

(defn query-multilang-dictionary [keys languages categories]
  (cond
    (and (seq languages) (seq categories)) (d/q '[:find (pull ?e [*])
                                                  :in $ [?keys ?languages ?categories]
                                                  :where [?e :dictionary-multilang/key ?key]
                                                  [?e :dictionary-multilang/language ?language]
                                                  [?e :dictionary-multilang/category ?category]
                                                  [(contains? ?categories ?category)]
                                                  [(contains? ?languages ?language)]
                                                  [(contains? ?keys ?key)]]
                                                (d/db conn)
                                                [(set keys) (set languages) (set categories)])
    (seq languages) (d/q '[:find (pull ?e [*])
                           :in $ [?keys ?languages]
                           :where [?e :dictionary-multilang/key ?key]
                           [?e :dictionary-multilang/language ?language]
                           [(contains? ?languages ?language)]
                           [(contains? ?keys ?key)]]
                         (d/db conn)
                         [(set keys) (set languages)])
    :else (d/q '[:find (pull ?e [*])
                 :in $ ?keys
                 :where [?e :dictionary-multilang/key ?key]
                 [(contains? ?keys ?key)]]
               (d/db conn)
               (set keys))))

(defmethod scan :dictionary-multilang [_ {:keys [keys languages categories]}]
  (map (fn [[item]] (read-multilang-dict-item item))
       (query-multilang-dictionary keys languages categories)))

(defmulti delete (fn [resource-type _] resource-type))

(defmethod delete :dictionary-combined [_ key]
  @(d/transact conn [[:db.fn/retractEntity [:dictionary-combined/id key]]])
  nil)

(defmethod delete :dictionary-multilang [_ key]
  @(d/transact conn [[:db.fn/retractEntity [:dictionary-multilang/id key]]])
  nil)


(defmethod delete :default [resource-type opts]
  (log/warnf "Default implementation of DELETE for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC DELETE FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti update! (fn [resource-type _ _] resource-type))

(defmethod update! :dictionary-combined [resource-type key data-item]
  (try
    @(d/transact conn [[:db.fn/retractEntity [:dictionary-combined/id key]]])
    @(d/transact conn [(remove-nil-vals (dissoc (prepare-dictionary-item key data-item) :db/id))])
    (catch Exception e
      (log/errorf "Error %s with data %s" e val)))
  (pull-entity resource-type key))

(defmethod update! :dictionary-multilang [resource-type key data-item]
  (try
    @(d/transact conn [[:db.fn/retractEntity [:dictionary-multilang/id key]]])
    @(d/transact conn [(remove-nil-vals (dissoc (prepare-multilang-dict key data-item) :db/id))])
    (catch Exception e
      (log/errorf "Error %s with data %s" e val)))
  (pull-entity resource-type key))

(defmethod update! :default [resource-type key data]
  (log/errorf "Default UPDATE for %s with key %s and %s" resource-type key data)
  (throw (RuntimeException. (format "DATOMIC UPDATE FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmethod delete :blockly [_ key]
  (blockly/delete conn key))
(defmethod pull-entity :blockly [_ key]
  (blockly/pull-entity conn key))
(defmethod update! :blockly [_ key data-item]
  (blockly/update! conn key data-item))
(defmethod scan :blockly [_ _]
  (blockly/scan conn))
(defmethod transact-item :blockly [_ key data-item]
  (blockly/transact-item conn key data-item))

(defn db-access
  [resource-type config]
  (log/debugf "Datomic for: %s with config %s" resource-type config)
  (reify
    protocol/DBAccess
    (read-item [this key]
      (pull-entity resource-type key))
    (write-item [this key data update-count?]
      (transact-item resource-type key data))
    (update-item [this key data]
      (try
        (update! resource-type key data)
        (catch Exception e
          (.printStackTrace e))))
    (delete-item [this key] (delete resource-type key))
    (list-items [this limit] (pull-n resource-type limit))
    (scan-items [this opts] (scan resource-type opts))
    (batch-read-items [this ids]
      (throw (RuntimeException. (format "DATOMIC BATCH-READ-ITEMS FOR '%s' NOT IMPLEMENTED" resource-type))))))
