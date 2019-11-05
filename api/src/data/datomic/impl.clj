(ns data.datomic.impl
  (:require [api.config :refer [conf]]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [data.protocol :as protocol]
            [data.utils :as utils]
            [datomic.api :as d]
            [io.rkn.conformity :as c]
            [mount.core :refer [defstate]])
  (:import (java.io File)
           (java.util UUID)))

(def schema-folder-name "datomic-schema")

(defn migrate [conn]
  (doseq [file-name (->> (file-seq (io/file (io/resource schema-folder-name)))
                         (remove #(.isDirectory ^File %))
                         (map #(.getName ^File %))
                         (sort))]
    (log/infof "Applying Datomic migration: %s" file-name)
    (c/ensure-conforms conn (c/read-resource (str schema-folder-name "/" file-name)))))

(defstate conn
  :start (let [c (d/connect (if-let [uri (:db-uri conf)]
                              uri
                              (let [uri (str "datomic:mem://" (str (UUID/randomUUID)))]
                                (d/create-database uri)
                                uri)))]
           (migrate c)
           c))

(defn remove-nil-vals [m] (into {} (remove (comp nil? second) m)))

(defn remove-empty-or-nil-vals [m]
  (into {}
        (remove (fn [[_ v]] (or (nil? v)
                                (and (not (boolean? v)) (empty? v))
                                (= [nil] v))) m)))

(defn remove-empty-or-nil-but-not-nil-list-vals [m]
  (into {}
        (remove (fn [[_ v]] (or (nil? v)
                                (and (not (boolean? v)) (empty? v)))) m)))

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

(defn prepare-document-plan [document-plan]
  (when document-plan
    (remove-empty-or-nil-vals
      {:blockly/segments        (map prepare-document-plan (:segments document-plan))
       :blockly/children        (map prepare-document-plan (:children document-plan))
       :blockly/hasChildren     (not (nil? (:children document-plan)))
       :blockly/srcId           (:srcId document-plan)
       :blockly/type            (:type document-plan)
       :blockly/name            (:name document-plan)
       :blockly/concept-id      (:conceptId document-plan)
       :blockly/item-id         (:itemId document-plan)
       :blockly/roles           (map prepare-document-plan (:roles document-plan))
       :blockly/child           (prepare-document-plan (:child document-plan))
       :blockly/dictionary-item (prepare-document-plan (:dictionaryItem document-plan))})))

(defmethod transact-item :blockly [_ key data-item]
  (let [current-ts (utils/ts-now)]
    @(d/transact conn [(remove-nil-vals
                         {:document-plan/id              key
                          :document-plan/uid             (:uid data-item)
                          :document-plan/data-sample-id  (:dataSampleId data-item)
                          :document-plan/name            (:name data-item)
                          :document-plan/blockly-xml     (:blocklyXml data-item)
                          :document-plan/document-plan   (prepare-document-plan (:documentPlan data-item))
                          :document-plan/created-at      current-ts
                          :document-plan/updated-at      current-ts
                          :document-plan/data-sample-row (:dataSampleRow data-item)
                          :document-plan/update-count    0})])
    (assoc data-item
      :id key
      :createdAt current-ts
      :updatedAt current-ts
      :updateCount 0)))

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

(defn doc-plan->document-plan [document-plan]
  (when (= (:blockly/name document-plan) "theme")
    (prn document-plan (if (and (:blockly/hasChildren document-plan)
                                (nil? (:blockly/children document-plan)))
                         [nil]
                         (map doc-plan->document-plan (:blockly/children document-plan)))))
  (when document-plan
    (remove-empty-or-nil-but-not-nil-list-vals
      {:segments       (map doc-plan->document-plan (:blockly/segments document-plan))
       :children       (if (and (:blockly/hasChildren document-plan)
                                (nil? (:blockly/children document-plan)))
                         [nil]
                         (map doc-plan->document-plan (:blockly/children document-plan)))
       :conceptId      (:blockly/concept-id document-plan)
       :srcId          (:blockly/srcId document-plan)
       :type           (:blockly/type document-plan)
       :name           (:blockly/name document-plan)
       :itemId         (:blockly/item-id document-plan)
       :child          (doc-plan->document-plan (:blockly/child document-plan))
       :roles          (map doc-plan->document-plan (:blockly/roles document-plan))
       :dictionaryItem (doc-plan->document-plan (:blockly/dictionary-item document-plan))})))

(defmethod pull-entity :blockly [_ key]
  (let [document-plan (ffirst (d/q '[:find (pull ?e [*])
                                     :in $ ?key
                                     :where [?e :document-plan/id ?key]]
                                   (d/db conn)
                                   key))]
    (when document-plan
      (remove-nil-vals
        {:id            (:document-plan/id document-plan)
         :uid           (:document-plan/uid document-plan)
         :name          (:document-plan/name document-plan)
         :blocklyXml    (:document-plan/blockly-xml document-plan)
         :documentPlan  (doc-plan->document-plan (:document-plan/document-plan document-plan))
         :createdAt     (:document-plan/created-at document-plan)
         :updatedAt     (:document-plan/updated-at document-plan)
         :dataSampleRow (:document-plan/data-sample-row document-plan)
         :updateCount   (:document-plan/update-count document-plan)}))))

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
       :results (:results/results entity)})))

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

(defmethod scan :blockly [_ _]
  (let [resp (first (d/q '[:find (pull ?e [*])
                           :where [?e :document-plan/id]]
                         (d/db conn)))]
    (map (fn [document-plan]
           {:id            (:document-plan/id document-plan)
            :uid           (:document-plan/uid document-plan)
            :name          (:document-plan/name document-plan)
            :blocklyXml    (:document-plan/blockly-xml document-plan)
            :documentPlan  (:document-plan/document-plan document-plan)
            :createdAt     (:document-plan/created-at document-plan)
            :updatedAt     (:document-plan/updated-at document-plan)
            :dataSampleRow (:document-plan/data-sample-row document-plan)
            :updateCount   (:document-plan/update-count document-plan)}) resp)))

(defmethod scan :default [resource-type opts]
  (log/warnf "Default implementation of SCAN for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC SCAN FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti delete (fn [resource-type _] resource-type))

(defmethod delete :blockly [_ key]
  @(d/transact conn [[:db.fn/retractEntity [:document-plan/id key]]])
  nil)

(defmethod delete :dictionary-combined [_ key]
  @(d/transact conn [[:db.fn/retractEntity [:dictionary-combined/id key]]])
  nil)

(defmethod delete :default [resource-type opts]
  (log/warnf "Default implementation of DELETE for the '%s' with key '%s'" resource-type opts)
  (throw (RuntimeException. (format "DATOMIC DELETE FOR '%s' NOT IMPLEMENTED" resource-type))))

(defmulti update! (fn [resource-type _ _] resource-type))

(defmethod update! :blockly [resource-type key data-item]
  (let [original (pull-entity resource-type key)
        current-ts (utils/ts-now)]
    @(d/transact conn [(remove-nil-vals
                         {:db/id                         [:document-plan/id key]
                          :document-plan/uid             (:uid data-item)
                          :document-plan/data-sample-id  (:dataSampleId data-item)
                          :document-plan/name            (:name data-item)
                          :document-plan/blockly-xml     (:blocklyXml data-item)
                          :document-plan/document-plan   (prepare-document-plan (:documentPlan data-item))
                          :document-plan/updated-at      current-ts
                          :document-plan/data-sample-row (:dataSampleRow data-item)
                          :document-plan/update-count    (inc (:updateCount original))})])
    (pull-entity resource-type key)))

(defmethod update! :results [_ key data-item]
  @(d/transact conn [(remove-nil-vals
                       {:db/id           [:results/id key]
                        :results/ready   (:ready data-item)
                        :results/error   (:error data-item)
                        :results/results (:results data-item)
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

(defn db-access
  [resource-type config]
  (log/debugf "Datomic for: %s with config %s" resource-type config)
  (reify
    protocol/DBAccess
    (read-item [this key]
      (pull-entity resource-type key))
    (write-item [this key data update-count?]
      (transact-item resource-type key data))
    (update-item [this key data] (update! resource-type key data))
    (delete-item [this key] (delete resource-type key))
    (list-items [this limit] (pull-n resource-type limit))
    (scan-items [this opts] (scan resource-type opts))
    (batch-read-items [this ids]
      (throw (RuntimeException. (format "DATOMIC BATCH-READ-ITEMS FOR '%s' NOT IMPLEMENTED" resource-type))))))
