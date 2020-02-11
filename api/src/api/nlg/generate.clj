(ns api.nlg.generate
  (:require [acc-text.nlg.utils.nlp :as nlp]
            [acc-text.nlg.core :as nlg]
            [acc-text.nlg.utils.ref-expressions :as ref-expr]
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
(s/def ::enrich boolean?)
(s/def ::format #{"raw" "dropoff" "annotated"})             ;; reitit does not convert these to keys
(s/def ::format-query (s/keys :opt-un [::format]))
(s/def ::dataRow (s/map-of string? string?))
(s/def ::dataRows (s/map-of ::key ::dataRow))
(s/def ::readerFlagValues (s/map-of string? boolean?))
(s/def ::generate-req (s/keys :req-un [::documentPlanId ::dataId]
                              :opt-un [::readerFlagValues ::enrich]))
(s/def ::generate-bulk (s/keys :req-un [::documentPlanId ::dataRows]
                               :opt-un [::readerFlagValues ::enrich]))

(defn get-data [data-id]
  (doall (utils/csv-to-map (data-files/read-data-file-content "example-user" data-id))))

(defn filter-empty [text] (not= "" text))

(defn merge-enrich-dupes [{:keys [original enriched] :as data}]
  (if (= original enriched)
    {:original original}
    data))

(defn generate-text
  ([document-plan data enrich] (generate-text document-plan data {:default true} enrich))
  ([document-plan data reader-model enrich]
   (let [languages      (cond-> []
                          (get reader-model "English"  false)    (conj :en)
                          (get reader-model "German"   false)    (conj :de)
                          (get reader-model "Estonian" false)    (conj :est)
                          (get reader-model "Latvian"  false)    (conj :lv))
         semantic-graph (parser/document-plan->semantic-graph document-plan)
         context (context/build-context semantic-graph reader-model)
         ref-expr-fn (partial ref-expr/apply-ref-expressions :en)
         enrich-data (into {} (map (fn [[k v]] {v (format "{%s}" (name k))}) data))
         enrich-fn (fn [text]
                     (cond-> {:original (ref-expr-fn text)}
                       enrich (assoc :enriched (ref-expr-fn
                                                (nlg/enrich-text enrich-data text)))))]
     (log/debugf "Languages: %s" languages)
     (log/debugf "Reader Model: %s" reader-model)
     (->> (nlg/generate-text semantic-graph (assoc context :data  data))
          (map :text)
          (sort)
          (dedupe)
          (filter filter-empty)
          (utils/inspect-results)
          (map enrich-fn)
          (map merge-enrich-dupes)))))

(defn generation-process [document-plan rows reader-model enrich]
  (try
    {:ready   true
     :results (doall (map (fn [[row-key data]]
                            [row-key (generate-text document-plan data reader-model enrich)])
                          rows))}
    (catch Exception e
      (log/errorf "Failed to generate text: %s" (utils/get-stack-trace e))
      {:error true :ready true :message (.getMessage e)})))

(defn generate-request [{document-plan-id :documentPlanId data-id :dataId reader-model :readerFlagValues enrich :enrich}]
  (let [result-id (utils/gen-uuid)
        {document-plan :documentPlan data-sample-row :dataSampleRow} (dp/get-document-plan document-plan-id)
        row (nth (get-data data-id) (or data-sample-row 0))]
    (results/store-status result-id {:ready false})
    (results/rewrite result-id (generation-process document-plan {:sample row} reader-model enrich))
    {:status 200
     :body   {:resultId result-id}}))

(defn generate-bulk [{document-plan-id :documentPlanId reader-model :readerFlagValues rows :dataRows enrich :enrich}]
  (let [result-id (utils/gen-uuid)
        {document-plan :documentPlan} (dp/get-document-plan document-plan-id)]
    (log/debugf "Bulk Generate request, data: %s" rows)
    (results/store-status result-id {:ready false})
    (results/rewrite result-id (generation-process document-plan rows reader-model enrich))
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
                         :children [{:type "SENTENCE"
                                     :id   (utils/gen-uuid)
                                     :children [{:type "WORD"
                                                 :id   (utils/gen-uuid)
                                                 :text r}]}]
                         ;; TODO: This was the logic:
                         ;; (for [sentence (nlp/split-into-sentences r)]
                                   ;;   {:type     "SENTENCE"
                                   ;;    :id       (utils/gen-uuid)
                                   ;;    :children (for [token (nlp/tokenize sentence)]
                                   ;;                {:type (nlp/token-type token)
                                   ;;                 :id   (utils/gen-uuid)
                                   ;;                 :text token})})
                         }]})
       results))

(defn prepend-lang-flag
  [text]
  ;; TODO: Harcoded EN flag at the moment. Should use flag of language used
  (format "🇬🇧 %s" text))

(defn transform-results
  [results]
  (mapcat (fn [{:keys [enriched original]}]
            (if enriched
              [(format "📔\t%s " original) (format "📙\t%s" enriched)]
              [original]))
          results))

(defn annotated-text-format [results]
  (->> results
       (map second)
       (flatten) ;; Don't care about any bulk keys at the moment
       (transform-results)
       (map prepend-lang-flag)
       (wrap-to-annotated-text)))

(defn raw-format [results]
  (into {} results))

(defn standoff-format [_])                                  ;; TODO

(defn error-response [exception]
  {:status 500
   :body   {:error   true
            :message (.getMessage exception)}})


(def dummy-response
  {:ready true
   :results [[:dummy [{:original (ref-expr/apply-ref-expressions :en "Test sentence one . Test sentence Two .")}]]
             [:dummy [{:original (ref-expr/apply-ref-expressions :en "Test sentence 12.3 one . Test sentence Two .")
                       :enriched (ref-expr/apply-ref-expressions :en "Test sentence one. Test sentence Two. Test sentence four. A very very long fith sentence test goes here. Test sentence six.")}]]]})

(defn read-result [{{:keys [path query]} :parameters}]
  (let [request-id (:id path)
        format-fn (case (keyword (:format query))
                    :raw raw-format
                    :standoff standoff-format
                    annotated-text-format)]
    (try
      (if-let [{:keys [results ready updatedAt]} dummy-response ;; (results/fetch request-id)
               ]
        {:status 200
         :body   {:offset     0
                  :totalCount (count (flatten results)) ;; Each key has N results. So flatten and count total
                  :ready      ready
                  :updatedAt  updatedAt
                  :variants   (format-fn results)}}
        {:status 404})
      (catch Exception e
        (log/errorf "Failed to read result with id `%s`: %s"
                    request-id (utils/get-stack-trace e))
        (error-response e)))))

(defn delete-result [{{request-id :id} :path-params}]
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
      (error-response e))))
