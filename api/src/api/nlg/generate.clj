(ns api.nlg.generate
  (:require [api.nlg.generator.planner-ng :as planner]
            [api.nlg.nlp :as nlp]
            [api.resource :as resource]
            [api.utils :as utils]
            [clojure.tools.logging :as log]
            [data.db.dynamo-ops :as ops]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as document-plan]
            [mount.core :refer [defstate]]))

(defstate results-db :start (ops/db-access :results))

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
    (ops/write! results-db result-id {:ready false})
    (ops/update! results-db result-id (generation-process document-plan-id data-id reader-model))
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

(defn read-result [_ path-params]
  (let [request-id (:id path-params)]
    (try
      (if-let [{:keys [results ready updatedAt]} (ops/read! results-db request-id)]
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

(defn delete-result [path-params]
  (let [request-id (:id path-params)]
    (try
      (if-let [item (ops/read! results-db request-id)]
        (do
          (ops/delete! results-db request-id)
          {:status 200
           :body   item})
        {:status 404})
      (catch Exception e
        (log/errorf "Failed to delete result with id `%s`: %s"
                    request-id (utils/get-stack-trace e))
        {:status 500
         :body   {:error   true
                  :message (.getMessage e)}}))))

(def handler-fn (delay
                  (resource/build-resource
                    {:get-handler    read-result
                     :post-handler   generate-request
                     :delete-handler delete-result}
                    true)))

(defn -handleRequest [_ is os _]
  (@handler-fn nil is os nil))
