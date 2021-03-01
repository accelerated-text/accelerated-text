(ns api.nlg.service
  (:require [api.nlg.core :refer [generate-text]]
            [api.nlg.format :refer [use-format with-default-format]]
            [api.nlg.service.request :as request]
            [api.nlg.service.utils :as utils]
            [api.utils :refer [gen-uuid]]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [data.spec.result :as result]
            [data.entities.reader-model :as reader-model]
            [data.entities.results :as results]))

(s/def ::generate-request
  (s/keys :opt-un [::request/documentPlanId
                   ::request/documentPlanName
                   ::request/dataId
                   ::request/dataRow
                   ::request/sampleMethod
                   ::request/readerFlagValues]))

(s/def ::generate-request-bulk
  (s/keys :req-un [::request/dataRows]
          :opt-un [::request/documentPlanId
                   ::request/documentPlanName
                   ::request/readerFlagValues]))

(s/def ::get-result
  (s/keys :opt-un [::request/format]))

(defn display-error? []
  (Boolean/valueOf (System/getenv "DISPLAY_ERROR")))

(defn generate-request
  [{data-id :dataId sample-method :sampleMethod data-row :dataRow reader-model :readerFlagValues :as request}]
  (try
    (log/infof "Generate request with %s" (utils/request->text request))
    (let [{row-index :dataSampleRow :as document-plan} (utils/get-document-plan request)
          result-id (gen-uuid)]
      (results/write #::result{:id     result-id
                               :status :pending})
      (results/write (generate-text {:id            result-id
                                     :document-plan document-plan
                                     :data          (or data-row (utils/get-data-row data-id (or sample-method "first") (or row-index 0)) {})
                                     :reader-model  (map reader-model/update! (utils/form-reader-model reader-model))}))
      {:status 200
       :body   {:resultId result-id}})
    (catch Exception e
      (utils/error-response e "Generate request failure"))))

(defn generate-request-bulk
  [{reader-model :readerFlagValues data-rows :dataRows :as request}]
  (try
    (log/infof "Bulk generate request with %s" (utils/request->text request))
    (let [document-plan (utils/get-document-plan request)]
      (doseq [result-id (keys data-rows)]
        (results/write #::result{:id     result-id
                                 :status :pending}))
      (->> data-rows
           (pmap (fn [[request-id data-row]]
                   (-> {:id            request-id
                        :document-plan document-plan
                        :data          data-row
                        :reader-model  (utils/form-reader-model reader-model)}
                       (generate-text)
                       (results/write))))
           (doall)
           (future))
      {:status 200
       :body   {:resultIds (keys data-rows)}})
    (catch Exception e
      (utils/error-response e "Bulk generate request failure"))))

(defn get-result [{{{request-id :id} :path {result-format :format} :query} :parameters}]
  (try
    (log/infof "Result request with id `%s`" request-id)
    (if-let [{::result/keys [rows status timestamp] :as result} (results/fetch request-id)]
      {:status 200
       :body   {:offset     0
                :totalCount (count rows)
                :ready      (not= status :pending)
                :updatedAt  timestamp
                :variants   (cond
                              (some? result-format) (use-format result-format result)
                              (and (= :error status) (display-error?)) (use-format "error" result)
                              :else (with-default-format result))}}
      (do
        (log/warnf "Result with id `%s` not found" request-id)
        {:status 404}))
    (catch Exception e
      (utils/error-response e (format "Failed to get result with id `%s`" request-id)))))

(defn delete-result [{{request-id :id} :path-params}]
  (try
    (log/infof "Delete result request with id `%s`" request-id)
    (if-let [item (results/fetch request-id)]
      (do
        (results/delete request-id)
        {:status 200
         :body   item})
      (do
        (log/warnf "Result with id `%s` not found" request-id)
        {:status 404}))
    (catch Exception e
      (utils/error-response e (format "Failed to delete result with id `%s`" request-id)))))
