(ns nlg.api.dictionary
  (:require [nlg.api.utils :as utils]
            [data-access.db.dynamo-ops :as ops]
            [nlg.api.resource :as resource]
            [clojure.tools.logging :as log]))


;; COPY-PASTE
(defn process-search-response [{:keys [status body] :as resp} error-return-value]
  (if (= 200 status)
    body
    (do (log/errorf "Failed to fetch dictionary items with response '%s'" resp)
        error-return-value)))

(defn list-dictionary-items []
  (-> (partial ops/list! (ops/db-access :dictionary))
      (utils/do-return nil)
      (process-search-response [])))

(defn dictionary-item [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :dictionary))
      (utils/do-return id)
      (process-search-response {})))

(defn phrase-usage-models [{:keys [ids]}]
  (-> (partial ops/batch-read! (ops/db-access :phrase-usage))
      (utils/do-return {:prim-kvs {:id ids}})
      (process-search-response [])))

(defn reader-flag-usages [{:keys [ids]}]
  (-> (partial ops/batch-read! (ops/db-access :reader-flag-usage))
      (utils/do-return {:prim-kvs {:id ids}})
      (process-search-response [])))

(defn reader-flag [{:keys [id]}]
  (-> (partial ops/read! (ops/db-access :reader-flag))
      (utils/do-return id)
      (process-search-response {})))

;; New logic

(defn get-phrases
  [id]
  (let [usage-models (-> (phrase-usage-models {:ids (-> (dictionary-item {:id id})
                                                        (:usageModels))})
                         (:phrase-usage-model))]
    (map
     (fn [model]
       (let [initial {:text (:phrase model)}
             parse-fn (fn [item]
                        {(keyword (:readerFlag item)) (keyword (:usage item))})
             readers (->> (reader-flag-usages {:ids (:readerUsage model)})
                          (:reader-flag-usage)
                          (map parse-fn))]
         (-> initial
             (assoc :flags (apply merge readers))
             (assoc-in [:flags :default] (keyword (:defaultUsage model)))))
       )
     usage-models)))

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
