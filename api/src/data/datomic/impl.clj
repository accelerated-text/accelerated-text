(ns data.datomic.impl
  (:require [api.config :refer [conf]]
            [clojure.tools.logging :as log]
            [data.protocol :as protocol]
            [data.utils :as utils]
            [datomic.api :as d]
            [mount.core :refer [defstate]]))

(defstate conn
  :start (d/connect (:db-uri conf)))

(defmulti transact-item (fn [resource-type _ _] resource-type))

(defmethod transact-item :data-files [_ key data-item]
  @(d/transact conn [{:data-file/id       key
                      :data-file/filename (:filename data-item)
                      :data-file/content  (:content data-item)}]))

(defmethod transact-item :dictionary-combined [_ key data-item]
  @(d/transact conn [(cond-> {:dictionary-combined/id key}
                             (:name data-item)
                             (assoc :dictionary-combined/name (:name data-item))
                             (:partOfSpeech data-item)
                             (assoc :dictionary-combined/partOfSpeech (:partOfSpeech data-item))
                             (seq (:phrases data-item))
                             (assoc :dictionary-combined/phrases (:phrases data-item)))]))

(defn remove-nil-vals [m]
  (into {} (remove (comp nil? second) m)))

(defmethod transact-item :blockly [_ key data-item]
  (let [current-ts (utils/ts-now)]
    @(d/transact conn [(remove-nil-vals
                         {:document-plan/id              key
                          :document-plan/uid             (:uid data-item)
                          :document-plan/data-sample-id  (:dataSampleId data-item)
                          :document-plan/name            (:name data-item)
                          :document-plan/blockly-xml     (:blocklyXml data-item)
                          :document-plan/document-plan   (:documentPlan data-item)
                          :document-plan/created-at      current-ts
                          :document-plan/updated-at      current-ts
                          :document-plan/data-sample-row (:dataSampleRow data-item)
                          :document-plan/update-count    0})])
    (assoc data-item
      :id key
      :createdAt current-ts
      :updatedAt current-ts
      :updateCount 0)))

(defmethod transact-item :default [resource-type key _]
  (log/warnf "Default implementation of transact-item for the '%s' with key '%s'"
             resource-type key)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defmulti pull-entity (fn [resource-type _] resource-type))

(defmethod pull-entity :data-files [_ key]
  (let [data-file (ffirst (d/q '[:find (pull ?e [*])
                                 :where
                                 [?e :data-file/id ?key]]
                               (d/db conn)
                               key))]
    (when data-file
      {:filename (:data-file/filename data-file)
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
       :phrases (:dictionary-combined/phrases dictionary-entry)})))

(defmethod pull-entity :blockly [_ key]
  (let [document-plan (ffirst (d/q '[:find (pull ?e [*])
                                     :in $ ?key
                                     :where [?e :document-plan/id ?key]]
                                   (d/db conn)
                                   key))]
    (when document-plan
      {:id           (:document-plan/id document-plan)
       :uid          (:document-plan/uid document-plan)
       :name         (:document-plan/name document-plan)
       :blocklyXml   (:document-plan/blockly-xml document-plan)
       :documentPlan (:document-plan/document-plan document-plan)
       :createdAt    (:document-plan/created-at document-plan)
       :updatedAt    (:document-plan/updated-at document-plan)
       :updateCount  (:document-plan/update-count document-plan)})))

(defmethod pull-entity :default [resource-type key]
  (log/warnf "Default implementation of pull-entity for the '%s' with key '%s'"
             resource-type key)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defmulti pull-n (fn [resource-type _] resource-type))

(defmethod pull-n :data-files [_ limit]
  (let [resp (first (d/q '[:find (pull ?e [*])
                           :where [?e :data-file/id]]
                         (d/db conn)))]

    (map (fn [df] {:id       (:data-file/id df)
                   :filename (:data-file/filename df)
                   :content  (:data-file/content df)}) (take limit resp))))

(defmethod pull-n :default [resource-type limit]
  (log/warnf "Default implementation of list-items for the '%s' with key '%s'"
             resource-type limit)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defmulti scan (fn [resource-type opts] resource-type))

(defmethod scan :blockly [resource-type opts]
  (log/warnf "Blockly of SCAN for the '%s' with key '%s'"
             resource-type opts)
  (let [resp (first (d/q '[:find (pull ?e [*])
                           :where [?e :document-plan/id]]
                         (d/db conn)))]
    (map (fn [document-plan]
           {:id           (:document-plan/id document-plan)
            :uid          (:document-plan/uid document-plan)
            :name         (:document-plan/name document-plan)
            :blocklyXml   (:document-plan/blockly-xml document-plan)
            :documentPlan (:document-plan/document-plan document-plan)
            :createdAt    (:document-plan/created-at document-plan)
            :updatedAt    (:document-plan/updated-at document-plan)
            :updateCount  (:document-plan/update-count document-plan)}) resp)))

(defmethod scan :default [resource-type opts]
  (log/warnf "Default implementation of SCAN for the '%s' with key '%s'"
             resource-type opts)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defmulti delete (fn [resource-type _] resource-type))

(defmethod delete :blockly [_ key]
  @(d/transact conn [[:db.fn/retractEntity [:document-plan/id key]]]))

(defmethod delete :default [resource-type opts]
  (log/warnf "Default implementation of DELETE for the '%s' with key '%s'"
             resource-type opts)
  (throw (RuntimeException. "NOT IMPLEMENTED")))

(defn db-access
  [resource-type config]
  (log/debugf "Datomic for: %s with config %s" resource-type config)
  (reify
    protocol/DBAccess
    (read-item [this key]
      (pull-entity resource-type key))
    (write-item [this key data update-count?]
      (transact-item resource-type key data))
    (update-item [this key data] (throw (RuntimeException. "NOT IMPLEMENTED")))
    (delete-item [this key] (delete resource-type key))
    (list-items [this limit] (pull-n resource-type limit))
    (scan-items [this opts] (scan resource-type opts))
    (batch-read-items [this ids] (throw (RuntimeException. "NOT IMPLEMENTED")))))
