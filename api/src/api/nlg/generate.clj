(ns api.nlg.generate
  (:require [acc-text.nlg.gf.builder :as gf-builder]
            [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.context :as context]
            [api.nlg.nlp :as nlp]
            [api.nlg.parser :as parser]
            [api.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as dp]
            [data.entities.result :as results]
            [jsonista.core :as json]
            [org.httpkit.client :as client]))

(s/def ::documentPlanId string?)
(s/def ::dataId string?)
(s/def ::readerFlagValues (s/map-of string? boolean?))
(s/def ::generate-req (s/keys :req-un [::documentPlanId ::dataId ::readerFlagValues]))

(defn get-data [data-id]
  (doall (utils/csv-to-map (data-files/read-data-file-content "example-user" data-id))))

(defn get-reader-profiles [reader-model]
  (or
    (seq
      (reduce-kv (fn [acc k v]
                   (cond-> acc
                           (true? v) (conj k)))
                 []
                 reader-model))
    [:default]))

(defn compile-request [grammar]
  @(client/request {:url     (or (System/getenv "GF_ENDPOINT") "http://localhost:8001")
                    :method  :post
                    :headers {"Content-type" "application/json"}
                    :body    (json/write-value-as-string {:content (reduce str grammar)})}))

(defn generate-templates [{graph ::sg/graph}]
  (-> (gf-builder/build-grammar graph)
      (compile-request)
      (get :body)
      (json/read-value utils/read-mapper)
      (get :results)))

(defn realize [text placeholders]
  (when-not (str/blank? text)
    (reduce-kv (fn [s k v]
                 (let [pattern (re-pattern (format "(?i)\\{\\{%s\\}\\}" (name k)))]
                   (str/replace s pattern v)))
               text
               placeholders)))

(defn postprocess [sentence]
  (reduce str (when-not (str/blank? sentence)
                (str/join [(str/capitalize (first sentence)) (apply str (rest sentence)) \.]))))

(defn take-rand [coll]
  (when (seq coll)
    (rand-nth coll)))

(defn generate-row [instance row]
  (->> instance
       (generate-templates)
       (map #(realize % row))
       (map postprocess)))

(defn generation-process [document-plan-id data-id reader-model]
  (try
    {:ready   true
     :results (let [{document-plan :documentPlan data-sample-row :dataSampleRow} (dp/get-document-plan document-plan-id)
                    row (nth (get-data data-id) (or data-sample-row 0))
                    semantic-graph (parser/document-plan->semantic-graph document-plan)
                    context (context/build-context semantic-graph (get-reader-profiles reader-model))]
                (-> semantic-graph
                    (sg/build-instances context)
                    (take-rand)
                    (generate-row row)))}
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
