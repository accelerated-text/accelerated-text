(ns api.nlg.service.utils
  (:require [api.config :refer [conf]]
            [api.nlg.enrich.data :as data-enrich]
            [api.nlg.format :refer [use-format with-default-format]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as dp]
            [data.entities.reader-model :as reader-model]
            [data.spec.result :as result]))

(defn translate-result [{::result/keys [id status timestamp rows] :as result} {result-format :format}]
  {:resultId   id
   :offset     0
   :totalCount (count rows)
   :ready      (not= status :pending)
   :updatedAt  timestamp
   :variants   (cond
                 (some? result-format) (use-format result-format result)
                 (and (= :error status) (:display-error conf)) (use-format "error" result)
                 :else (with-default-format result))})

(defn error-response
  ([exception] (error-response exception nil))
  ([exception custom-message]
   (let [message (->> [custom-message (.getMessage exception)] (filter some?) (str/join ": "))]
     (log/error message)
     (log/trace (str/join "\n" (.getStackTrace exception)))
     {:status 500
      :body   {:error   true
               :message message}})))

(defn request->text [{:keys [documentPlanId documentPlanName dataId readerFlagValues]}]
  (->> [(when (some? documentPlanId)
          (format "document plan id `%s`" documentPlanId))
        (when (and (some? documentPlanName) (nil? documentPlanId))
          (format "document plan name `%s`" documentPlanName))
        (when (some? dataId)
          (format "data id `%s`" dataId))
        (when-let [active-reader-flags (seq (map first (filter (comp true? second) readerFlagValues)))]
          (format "reader flags: `%s`" (str/join "`, `" active-reader-flags)))]
       (remove nil?)
       (str/join "; ")))

(defn get-data-row [data-id sample-method index group-id]
  (when-not (str/blank? data-id)
    (if-let [{[{fields :fields}] :records filename :fileName} (case sample-method
                                                                "relevant" (data-files/fetch-most-relevant data-id index 20 group-id)
                                                                "first" (data-files/fetch data-id index 1 group-id))]
      (cond->> (zipmap (map :fieldName fields) (map :value fields))
               (data-enrich/enable-enrich?) (data-enrich/enrich filename))
      (log/errorf "Data with id `%s` not found" data-id))))

(defn get-document-plan [{id :documentPlanId name :documentPlanName} group-id]
  (cond
    (some? id) (dp/get-document-plan id)
    (some? name) (some #(when (= name (:name %)) %) (dp/list-document-plans "Document" group-id))
    :else (throw (Exception. "Must provide either document plan id or document plan name."))))

(defn form-reader-model [reader-model]
  (map (fn [[code enabled?]]
         (if-let [rm (reader-model/fetch code)]
           (assoc rm :data.spec.reader-model/enabled? enabled?)
           (throw (Exception. (format "Unknown reader model: `%s`" code)))))
       reader-model))
