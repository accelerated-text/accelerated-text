(ns api.nlg.service
  (:require [api.nlg.core :refer [generate-text]]
            [api.nlg.format :refer [use-format with-default-format]]
            [api.nlg.service.request :as request]
            [api.nlg.service.utils :refer [error-response get-data-row reader-model->languages]]
            [api.utils :as utils]
            [clojure.spec.alpha :as s]
            [data.spec.result :as result]
            [data.entities.dictionary :refer [default-language]]
            [data.entities.document-plan :as dp]
            [data.entities.results :as results]
            [clojure.tools.logging :as log]))

(s/def ::generate-request
  (s/keys :req-un [::request/documentPlanId]
          :opt-un [::request/dataId ::request/dataRow ::request/readerFlagValues]))

(s/def ::generate-request-bulk
  (s/keys :req-un [::request/documentPlanId ::request/dataRows]
          :opt-un [::request/readerFlagValues]))

(s/def ::get-result
  (s/keys :opt-un [::request/format]))

(defn generate-request
  [{document-plan-id :documentPlanId
    data-id          :dataId
    data-row         :dataRow
    reader-model     :readerFlagValues :as request}]
  (log/infof "Generate request `%s`" request)
  (try
    (let [{document-plan :documentPlan row-index :dataSampleRow} (dp/get-document-plan document-plan-id)
          languages (or (reader-model->languages reader-model) [(default-language)])
          result-id (utils/gen-uuid)]
      (results/write #::result{:id     result-id
                               :status :pending})
      (results/write (generate-text {:id            result-id
                                     :document-plan document-plan
                                     :data          (or data-row (get-data-row data-id (or row-index 0)))
                                     :languages     languages}))
      {:status 200
       :body   {:resultId result-id}})
    (catch Exception e
      (error-response e "Generate request failure"))))

(defn generate-request-bulk
  [{document-plan-id :documentPlanId
    reader-model     :readerFlagValues
    data-rows        :dataRows}]
  (try
    (let [{document-plan :documentPlan} (dp/get-document-plan document-plan-id)
          languages (or (reader-model->languages reader-model) [(default-language)])
          result-id (utils/gen-uuid)]
      (doseq [[request-id data-row] data-rows]
        (results/write #::result{:id     result-id
                                 :status :pending})
        (results/write (generate-text {:id            request-id
                                       :document-plan document-plan
                                       :data          data-row
                                       :languages     languages})))
      {:status 200
       :body   {:resultIds (keys data-rows)}})
    (catch Exception e
      (error-response e "Bulk generate request failure"))))

(defn get-result [{{{request-id :id} :path {result-format :format} :query} :parameters}]
  (try
    (if-let [{::result/keys [rows status timestamp] :as result} (results/fetch request-id)]
      {:status 200
       :body   {:offset     0
                :totalCount (count rows)
                :ready      (not= status :pending)
                :updatedAt  timestamp
                :variants   (if (some? result-format)
                              (use-format result-format result)
                              (with-default-format result))}}
      {:status 404})
    (catch Exception e
      (error-response e (format "Failed to read result with id `%s`" request-id)))))

(defn delete-result [{{request-id :id} :path-params}]
  (try
    (if-let [item (results/fetch request-id)]
      (do
        (results/delete request-id)
        {:status 200
         :body   item})
      {:status 404})
    (catch Exception e
      (error-response e (format "Failed to delete result with id `%s`" request-id)))))
