(ns api.nlg.generate
  (:require [acc-text.nlg.utils.nlp :as nlp]
            [acc-text.nlg.core :as nlg]
            [api.nlg.context :as context]
            [api.nlg.parser :as parser]
            [api.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as dp]
            [data.entities.result :as results]))

(s/def ::documentPlanId string?)
(s/def ::key string?)
(s/def ::dataId string?)
(s/def ::format #{"raw" "dropoff" "annotated"})             ;; reitit does not convert these to keys
(s/def ::format-query (s/keys :opt-un [::format]))
(s/def ::dataRow (s/map-of string? string?))
(s/def ::dataRows (s/map-of ::key ::dataRow))
(s/def ::readerFlagValues (s/map-of string? boolean?))
(s/def ::generate-req (s/keys :req-un [::documentPlanId ::dataId]
                              :opt-un [::readerFlagValues]))
(s/def ::generate-bulk (s/keys :req-un [::documentPlanId ::dataRows]
                               :opt-un [::readerFlagValues]))

(defn get-data [data-id]
  (doall (utils/csv-to-map (data-files/read-data-file-content "example-user" data-id))))

(defn generate-row
  ([document-plan data] (generate-row document-plan data {:default true}))
  ([document-plan data reader-model]
   (let [semantic-graph (parser/document-plan->semantic-graph document-plan)
         context (context/build-context semantic-graph reader-model)]
     (->> (nlg/generate-text semantic-graph context data)
          (map :text)
          (sort)
          (dedupe)))))

(defn generation-process [document-plan rows reader-model]
  (try
    {:ready   true
     :results (doall (map (fn [[row-key data]]
                            [row-key (generate-row document-plan data reader-model)])
                          rows))}
    (catch Exception e
      (log/errorf "Failed to generate text: %s" (utils/get-stack-trace e))
      {:error true :ready true :message (.getMessage e)})))

(defn generate-request [{document-plan-id :documentPlanId data-id :dataId reader-model :readerFlagValues}]
  (let [result-id (utils/gen-uuid)
        {document-plan :documentPlan data-sample-row :dataSampleRow} (dp/get-document-plan document-plan-id)
        row (nth (get-data data-id) (or data-sample-row 0))]
    (results/store-status result-id {:ready false})
    (results/rewrite result-id (generation-process document-plan {:sample row} reader-model))
    {:status 200
     :body   {:resultId result-id}}))

(defn generate-bulk [{document-plan-id :documentPlanId reader-model :readerFlagValues rows :dataRows}]
  (let [result-id (utils/gen-uuid)
        {document-plan :documentPlan} (dp/get-document-plan document-plan-id)]
    (log/debugf "Bulk Generate request, data: %s" rows)
    (results/store-status result-id {:ready false})
    (results/rewrite result-id (generation-process document-plan rows reader-model))
    {:status 200
     :body   {:resultId result-id}}))

(defn wrap-to-annotated-text
  [results]
  (map (fn [r]
         {:type        "ANNOTATED_TEXT"
          :id          (utils/gen-uuid)
          :annotations []
          :references  []
          :children    [{:type     "PARAGRAPH"
                         :id       (utils/gen-uuid)
                         :children (vec
                                     (for [sentence (nlp/split-into-sentences r)]
                                       {:type     "SENTENCE"
                                        :id       (utils/gen-uuid)
                                        :children (vec
                                                    (for [token (nlp/tokenize sentence)]
                                                      {:type (nlp/token-type token)
                                                       :id   (utils/gen-uuid)
                                                       :text token}))}))}]})
       results))

(defn annotated-text-format [results]
  (->> results
       (map second)
       (flatten)                                            ;; Don't care about any bulk keys at the moment
       (wrap-to-annotated-text)))

(defn raw-format [results]
  (into {} results))

(defn standoff-format [_])                                  ;; TODO

(defn read-result [{{:keys [path query]} :parameters}]
  (let [request-id (:id path)
        format-fn (case (keyword (:format query))
                    :raw raw-format
                    :standoff standoff-format
                    annotated-text-format)]
    (try
      (if-let [{:keys [results ready updatedAt]} (results/fetch request-id)]
        {:status 200
         :body   {:offset     0
                  :totalCount (count (flatten results))     ;; Each key has N results. So flatten and count total
                  :ready      ready
                  :updatedAt  updatedAt
                  :variants   (format-fn results)}}
        {:status 404})
      (catch Exception e
        (log/errorf "Failed to read result with id `%s`: %s"
                    request-id (utils/get-stack-trace e))
        {:status 500
         :body   {:error   true
                  :message (.getMessage e)}}))))

(defn delete-result [{:keys [path-params]}]
  (let [request-id (:id path-params)]
    (try
      (if-let [item (results/fetch request-id)]
        (do
          (results/delete request-id)
          {:status 200
           :body   item})
        {:status 404})
      (catch Exception e
        (log/errorf "Failed to delete result with id `%s`: %s"
                    request-id (utils/get-stack-trace e))
        {:status 500
         :body   {:error   true
                  :message (.getMessage e)}}))))
