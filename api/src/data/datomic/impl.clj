(ns data.datomic.impl
  (:require [api.config :refer [conf]]
            [clojure.tools.logging :as log]
            [data.protocol :as protocol]
            [datomic.api :as d]
            [mount.core :refer [defstate]]
            [data.datomic.utils :as utils :refer [remove-nil-vals]]
            [data.datomic.blockly :as blockly]
            [jsonista.core :as json]))

(def read-mapper (json/object-mapper {:decode-key-fn true}))

(defn encode-results [results]
  (map json/write-value-as-string results))

(defn decode-results [results]
  (map #(json/read-value % read-mapper) results))

(defstate conn
  :start (utils/get-conn conf))

(defmulti transact-item (fn [resource-type _ _] resource-type))

(defmethod transact-item :data-files [_ key data-item]
  @(d/transact conn [{:data-file/id       key
                      :data-file/filename (:filename data-item)
                      :data-file/content  (:content data-item)}]))

(defn prepare-dictionary-item [key data-item]
  {:db/id                            [:dictionary-combined/id key]
   :dictionary-combined/id           key
   :dictionary-combined/name         (:name data-item)
   :dictionary-combined/partOfSpeech (:partOfSpeech data-item)
   :dictionary-combined/phrases
                                     (->> (:phrases data-item)
                                          (map (fn [phrase]
                                                 (remove-nil-vals
                                                   {:phrase/id    (:id phrase)
                                                    :phrase/text  (:text phrase)
                                                    :phrase/flags (let [flgs (:flags phrase)]
                                                                    (when flgs
                                                                      {:reader-flag/default (:default flgs)}))})))
                                          (remove empty?))})

(defmethod transact-item :dictionary-combined [_ key data-item]
  (try
    @(d/transact conn [(remove-nil-vals (dissoc (prepare-dictionary-item key data-item) :db/id))])
    (assoc data-item :key key)
    (catch Exception e (.printStackTrace e))))

(defmethod transact-item :results [_ key data-item]
  @(d/transact conn [(remove-nil-vals
                       {:results/id key
                        :results/ready (:ready data-item)})]))

(defmethod transact-item :default [resource-type key _]
  (log/warnf "Default implementation of transact-item for the '%s' with key '%s'" resource-type key)
  (throw (RuntimeException. (format "DATOMIC TRANSACT-ITEM FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti pull-entity (fn [resource-type _] resource-type))

(defmethod pull-entity :data-files [_ key]
  (let [data-file (ffirst (d/q '[:find (pull ?e [*])
                                 :where
                                 [?e :data-file/id ?key]]
                               (d/db conn)
                               key))]
    (when data-file
      {:id       (:data-file/id data-file)
       :filename (:data-file/filename data-file)
       :content  (:data-file/content data-file)})))

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
                                   :flags {:default (:reader-flag/default (:phrase/flags phrase))}})
                     (:dictionary-combined/phrases dictionary-entry))})))



(defmethod pull-entity :results [_ key]
  (let [entity (ffirst (d/q '[:find (pull ?e [*])
                              :where
                              [?e :results/id ?key]]
                            (d/db conn)
                            key))]
    (when entity
      {:id      (:results/id key)
       :ready   (:results/ready entity)
       :error   (:results/error entity)
       :message (:results/message entity)
       :results (decode-results (:results/results entity))})))

(defmethod pull-entity :default [resource-type key]
  (log/warnf "Default implementation of pull-entity for the '%s' with key '%s'" resource-type key)
  (throw (RuntimeException. (format "DATOMIC PULL-ENTITY FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti pull-n (fn [resource-type _] resource-type))

(defmethod pull-n :data-files [_ limit]
  (let [resp (first (d/q '[:find (pull ?e [*])
                           :where [?e :data-file/id]]
                         (d/db conn)))]

    (map (fn [df] {:id       (:data-file/id df)
                   :filename (:data-file/filename df)
                   :content  (:data-file/content df)}) (take limit resp))))

(defmethod pull-n :reader-flag [_ limit]
  (take limit (first (d/q '[:find (pull ?e [*])
                            :where [?e :reader-flag/default]]
                          (d/db conn)))))

(defmethod pull-n :dictionary-combined [_ limit]
  (map (fn [item]
         {:key          (:dictionary-combined/id item)
          :name         (:dictionary-combined/name item)
          :partOfSpeech (:dictionary-combined/partOfSpeech item)
          :phrases      (map (fn [phrase] {:id    (:phrase/id phrase)
                                           :text  (:phrase/text phrase)
                                           :flags {:default (:reader-flag/default (:phrase/flags phrase))}})
                             (:dictionary-combined/phrases item))})
       (take limit (first (d/q '[:find (pull ?e [*])
                                 :where [?e :dictionary-combined/id]]
                               (d/db conn))))))

(defmethod pull-n :default [resource-type limit]
  (log/warnf "Default implementation of list-items for the '%s' with key '%s'" resource-type limit)
  (throw (RuntimeException. (format "DATOMIC PULL-N FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti scan (fn [resource-type _] resource-type))

(defmethod scan :default [resource-type opts]
  (log/warnf "Default implementation of SCAN for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC SCAN FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti delete (fn [resource-type _] resource-type))

(defmethod delete :dictionary-combined [_ key]
  @(d/transact conn [[:db.fn/retractEntity [:dictionary-combined/id key]]])
  nil)

(defmethod delete :default [resource-type opts]
  (log/warnf "Default implementation of DELETE for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC DELETE FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti update! (fn [resource-type _ _] resource-type))

(defmethod update! :results [_ key data-item]
  @(d/transact conn [(remove-nil-vals
                       {:db/id           [:results/id key]
                        :results/ready   (:ready data-item)
                        :results/error   (:error data-item)
                        :results/results (encode-results (:results data-item))
                        :results/message (:message data-item)})]))

(defmethod update! :dictionary-combined [resource-type key data-item]
  (let [val [(remove-nil-vals (prepare-dictionary-item key data-item))]]
    (try
      @(d/transact conn val)
      (catch Exception e
        (log/errorf "Error %s with data %s" e val)))
    (pull-entity resource-type key)))

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
