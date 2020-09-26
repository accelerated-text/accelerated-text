(ns api.nlg.service.utils
  (:require [api.nlg.enrich.data :as data-enrich]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as dp]
            [data.entities.language :as lang]))

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

(defn get-data-row [data-id index]
  (when-not (str/blank? data-id)
    (if-let [{[{fields :fields}] :records filename :fileName} (data-files/fetch data-id index 1)]
      (cond->> (zipmap (map :fieldName fields) (map :value fields))
               (data-enrich/enable-enrich?) (data-enrich/enrich filename))
      (log/errorf "Data with id `%s` not found" data-id))))

(defn get-document-plan [{id :documentPlanId name :documentPlanName}]
  (cond
    (some? id) (dp/get-document-plan id)
    (some? name) (some #(when (= name (:name %)) %) (dp/list-document-plans "Document"))
    :else (throw (Exception. "Must provide either document plan id or document plan name."))))

(defn reader-model->languages [reader-model]
  (map (fn [[code enabled?]]
         (lang/get-language code enabled?))
       reader-model))
