(ns api.nlg.service.utils
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.dictionary :as dict-entity]
            [data.entities.document-plan :as dp]))

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
          (format "document plan name `%s`" documentPlanId))
        (if (some? dataId)
          (format "data id `%s`" dataId))
        (when-let [active-reader-flags (seq (map first (filter (comp true? second) readerFlagValues)))]
          (format "reader flags: `%s`" (str/join "`, `" active-reader-flags)))]
       (remove nil?)
       (str/join "; ")))

(defn reader-model->languages [reader-model]
  (reduce-kv (fn [languages reader-flag state]
               (let [lang (dict-entity/flag->lang reader-flag)]
                 (if (some? lang)
                   (cond-> languages (true? state) (conj lang))
                   (do
                     (log/warnf "Unknown reader flag: `%s`" reader-flag)
                     languages))))
             nil
             reader-model))

(defn get-data-row [data-id index]
  (when-not (str/blank? data-id)
    (when-let [data (data-files/get-data "user" data-id (log/errorf "Data with id `%s` not found" data-id))]
      (nth data index (log/errorf "Data with id `%s` does not contain row index %s" data-id index)))))

(defn get-document-plan [{id :documentPlanId name :documentPlanName}]
  (cond
    (some? id) (dp/get-document-plan id)
    (some? name) (some #(when (= name (:name %)) %) (dp/list-document-plans "Document"))
    :else (throw (Exception. "Must provide either document plan id or document plan name."))))
