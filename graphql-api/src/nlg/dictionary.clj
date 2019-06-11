(ns nlg.dictionary
  (:require [clojure.tools.logging :as log]
            [db.dynamo-ops :as ops]
            [nlg.utils :as utils])
  (:import (java.util UUID)))

(defn- generate-UUID [] (.toString (UUID/randomUUID)))

(defn process-response [{:keys [status body] :as resp} error-return-value]
  (if (= 200 status)
    body
    (do (log/errorf "Failed to process `DictionaryItem` or its components response '%s'" resp)
        error-return-value)))

(defn list-dictionary-items []
  (-> (partial ops/list! (ops/db-access :dictionary))
      (utils/do-return nil)
      (process-response [])))

(defn dictionary-item [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :dictionary))
      (utils/do-return {:id id})
      (process-response {})))

(defn phrase-usage-model [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :phrase-usage))
      (utils/do-return {:id id})
      (process-response {})))

(defn phrase-usage-models [{:keys [ids]}]
  (-> (partial ops/batch-read! (ops/db-access :phrase-usage))
      (utils/do-return {:prim-kvs {:id ids}})
      (process-response [])))

(defn reader-flag-usages [{:keys [ids]}]
  (-> (partial ops/batch-read! (ops/db-access :reader-flag-usage))
      (utils/do-return {:prim-kvs {:id ids}})
      (process-response [])))

(defn reader-flag [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :reader-flag))
      (utils/do-return {:id id})
      (process-response {})))

(defn list-reader-flags []
  (-> (partial ops/list! (ops/db-access :reader-flag))
      (utils/do-return nil)
      (process-response [])))

(defn update-reader-flag-usage [{:keys [id usage]}]
  (-> (partial ops/update! (ops/db-access :reader-flag-usage))
      (utils/do-update {:id id} {:usage usage})
      (process-response {})))

(defn update-phrase-usage [{:keys [id defaultUsage]}]
  (-> (partial ops/update! (ops/db-access :phrase-usage))
      (utils/do-update {:id id} {:defaultUsage defaultUsage})
      (process-response {})))

(defn create-reader-usage-model [{:keys [phrase usage flag-id]}]
  (-> (partial ops/write! (ops/db-access :reader-flag-usage))
      (utils/do-update {:id         (generate-UUID)
                        :phrase     phrase
                        :usage      usage
                        :readerFlag flag-id})
      (process-response {})))

(defn create-phrase-usage-model [{:keys [phrase defaultUsage]}]
  (let [reader-flag-ids (map :id (list-reader-flags))
        reader-usage-model-ids (mapv #(:id (create-reader-usage-model {:phrase  phrase
                                                                      :usage   :DONT_CARE
                                                                      :flag-id %}))
                                    reader-flag-ids)]
    (-> (partial ops/write! (ops/db-access :phrase-usage))
        (utils/do-update {:id           (generate-UUID)
                          :phrase       phrase
                          :defaultUsage defaultUsage
                          :readerUsage  reader-usage-model-ids})
        (process-response {}))))

(defn update-dictionary-item-usage-models [{:keys [id usageModels]}]
  (-> (partial ops/update! (ops/db-access :dictionary))
      (utils/do-update {:id id} {:usageModels usageModels})
      (process-response {})))

(defn delete-reader-flag-usage-model [{:keys [id]}]
  (process-response (utils/do-delete (partial ops/read! (ops/db-access :reader-flag-usage))
                                     (partial ops/delete! (ops/db-access :reader-flag-usage))
                                     {:id id})
                    {}))

(defn delete-phrase-usage-model [{:keys [id]}]
  (doseq [reader-usage-id (:readerUsage (phrase-usage-model {:id id}))]
    (delete-reader-flag-usage-model {:id reader-usage-id}))
  (process-response (utils/do-delete (partial ops/read! (ops/db-access :phrase-usage))
                                     (partial ops/delete! (ops/db-access :phrase-usage))
                                     {:id id})
                    {}))

(defn dictionary-item-id-that-contains-phrase-model [{:keys [id]}]
  (let [dict-items (list-dictionary-items)]
    (loop [dictionary-item (first dict-items) remaining (rest dict-items)]
     (if dictionary-item
       (if (some #(= id %) (:usageModels dictionary-item))
         (:id dictionary-item)
         (recur (first remaining) (rest remaining)))
       (throw (Exception. (format "No 'dictionary-item' found that contains 'phrase-usage-model' with id: `%s`" id)))))))
