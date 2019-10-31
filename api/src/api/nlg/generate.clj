(ns api.nlg.generate
  (:require [api.nlg.generator.planner-ng :as planner]
            [api.nlg.nlp :as nlp]
            [api.utils :as utils]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as document-plan]
            [clojure.spec.alpha :as s]
            [data.entities.result :as results]))

(s/def ::documentPlanId string?)
(s/def ::dataId string?)
(s/def ::readerFlagValues (s/map-of string? boolean?))
(s/def ::generate-req (s/keys :req-un [::documentPlanId ::dataId ::readerFlagValues]))

(defn get-data [data-id]
  (doall (utils/csv-to-map (data-files/read-data-file-content "example-user" data-id))))

(def default-reader-model
  {:junior false
   :senior false})

(defn generation-process [dp-id data-id reader-model]
  (try
    {:ready   true
     :results (planner/render-dp
                (-> dp-id (document-plan/get-document-plan) (get :documentPlan))
                (get-data data-id)
                (if (seq reader-model)
                  reader-model
                  default-reader-model))}
    (catch Exception e
      (log/errorf "Failed to generate text: %s" (utils/get-stack-trace e))
      {:error true :ready true :message (.getMessage e)})))

(defn generate-request [{document-plan-id :documentPlanId data-id :dataId reader-model :readerFlagValues}]
  (let [result-id (utils/gen-uuid)]
    (results/store-status result-id {:ready false})
    (results/rewrite result-id (generation-process document-plan-id data-id reader-model))
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

(defn read-result [{:keys [path-params]}]
  (let [request-id (:id path-params)]
    (try
      (if-let [{:keys [results ready updatedAt]} (results/fetch request-id)]
        {:status 200
         :body   {:offset     0
                  :totalCount (count results)
                  :ready      ready
                  :updatedAt  updatedAt
                  :variants   (wrap-to-annotated-text results)}}
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
