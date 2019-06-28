(ns nlg.api.generate
  (:require [clojure.tools.logging :as log]
            [nlg.api.utils :as utils]
            [data-access.db.dynamo-ops :as ops]
            [data-access.db.s3 :as s3]
            [data-access.db.config :as config]
            [nlg.generator.planner-ng :as planner]
            [nlg.api.resource :as resource])
  (:import (java.io BufferedWriter))
  (:gen-class
    :name nlg.api.NLGHandler
    :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn get-db [] (ops/db-access :results))

(defn get-data
  [data-id]
  (let [raw (s3/read-file config/data-bucket data-id)
        csv (doall (utils/csv-to-map raw))]
    csv))

(defn generation-process
  [dp-id data-id result-fn reader-model]
  (let [data (get-data data-id)
        dp (-> (ops/get-workspace dp-id)
               :documentPlan)
        results (utils/result-or-error (planner/render-dp dp data reader-model))
        body (if (map? results)
               results
               {:ready true
                :results (when (not (empty? results))
                           (vec results))})]
    (log/debugf "Body: %s" body)
    (result-fn body)))

(def default-reader-model
  {:junior false
   :senior false})


(defn generate-request [request-body]
  (let [db               (get-db)
        document-plan-id (request-body :documentPlanId)
        data-id          (request-body :dataId)
        reader-model     (get request-body :readerFlagValues default-reader-model)
        result-id        (utils/gen-uuid)
        _                (ops/update! db result-id {:ready false})
        result-fn        (fn [body] (ops/update! db result-id body))
        _                @(future (generation-process document-plan-id data-id result-fn reader-model))]
    (utils/do-return (fn [] {:resultId result-id}))))

(defn wrap-to-annotated-text
  [results]
  {:type "ANNOTATED_TEXT"
   :id (utils/gen-uuid)
   :annotations []
   :references []
   :children [{:type "PARAGRAPH"
               :id (utils/gen-uuid)
               :children (map (fn [r]
                                {:type "OUTPUT_TEXT"
                                 :id (utils/gen-uuid)
                                 :text r}) results)}]
   }
)

(defn read-result [query-params path-params]
  (let [db (get-db)
        request-id (path-params :id)
        gen-result (ops/read! db request-id)
        body {:offset 0
              :totalCount 123
              :ready (gen-result :ready)
              :updatedAt (gen-result :updatedAt)
              :variants (list (wrap-to-annotated-text (gen-result :results)))}]
    (utils/do-return (fn [] body))))

(defn delete-result [path-params]
  (let [db (get-db)
        request-id (path-params :id)]
    (utils/do-delete (partial ops/read! db) (partial ops/delete! db) request-id)))

(def -handleRequest
  (resource/build-resource {:get-handler read-result
                            :post-handler generate-request
                            :delete-handler delete-result}
                           true))
