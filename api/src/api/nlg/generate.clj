(ns api.nlg.generate
  (:require [acc-text.nlg.core :as nlg]
            #_[acc-text.nlp.ref-expressions :refer [apply-ref-expressions]]
            [api.nlg.context :as context]
            [api.nlg.enrich :refer [enrich-texts]]
            [api.nlg.parser :as parser]
            [api.utils :as utils]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [data.entities.data-files :as data-files]
            [data.entities.document-plan :as dp]
            [data.entities.result :as results]
            [data.entities.dictionary :as dict-entity]))

(s/def ::documentPlanId string?)
(s/def ::key string?)
(s/def ::dataId string?)
(s/def ::enrich boolean?)
(s/def ::async boolean?)
(s/def ::format #{"raw" "dropoff" "annotated"})             ;; reitit does not convert these to keys
(s/def ::format-query (s/keys :opt-un [::format]))
(s/def ::dataRow (s/map-of string? string?))
(s/def ::dataRows (s/map-of ::key ::dataRow))
(s/def ::readerFlagValues (s/map-of string? boolean?))
(s/def ::generate-req (s/keys :opt-un [::documentPlanId ::documentPlanName ::dataId ::dataRow ::readerFlagValues ::enrich ::async]))
(s/def ::generate-bulk (s/keys :req-un [::dataRows]
                               :opt-un [::documentPlanId ::documentPlanName ::readerFlagValues ::enrich]))

(defn get-data [data-id]
  (doall (utils/csv-to-map (data-files/read-data-file-content "example-user" data-id))))

(defn get-document-plan [{id :documentPlanId name :documentPlanName}]
  (cond
    (some? id) (dp/get-document-plan id)
    (some? name) (some #(when (= name (:name %)) %) (dp/list-document-plans "Document"))
    :else (throw (Exception. "Must provide either document plan id or document plan name."))))

(defn generate-text-for-language [semantic-graph context enrich lang]
  (->> (nlg/generate-text semantic-graph context lang)
       (map :text)
       (sort)
       (dedupe)
       (remove str/blank?)
       (map (comp
              #(cond-> {:lang     lang
                        :original %}
                       (true? enrich) (assoc :enriched (sort (set/difference
                                                               (set (enrich-texts % (:data context))) %))))
              #_(apply-ref-expressions lang %)))))

(defn reader-model->languages [reader-model]
  (reduce-kv (fn [acc k v]
               (cond-> acc (true? v) (conj (dict-entity/flag->lang k))))
             []
             reader-model))

(defn generate-text
  ([document-plan data enrich] (generate-text document-plan data {(dict-entity/default-language-flag) true} enrich))
  ([document-plan data reader-model enrich]
   (let [languages (reader-model->languages reader-model)
         semantic-graph (parser/document-plan->semantic-graph document-plan)
         context (context/build-context semantic-graph {:languages languages :data data})]
     (mapcat (fn [lang]
               (generate-text-for-language semantic-graph (update context :dictionary #(get % lang {})) enrich lang))
             languages))))

(defn generation-process [document-plan rows reader-model enrich]
  (try
    {:ready   true
     :results (doall (map (fn [[row-key data]]
                            (log/debugf "Generating result for row-key: %s, data: %s" row-key data)
                            [row-key (generate-text document-plan data reader-model enrich)])
                          rows))}
    (catch Exception e
      (log/errorf "Failed to generate text: %s" e)
      (log/trace (utils/get-stack-trace e))
      {:error true :ready true :message (.getMessage e)})))

(defn generate-request [{data-id :dataId data-row :dataRow reader-model :readerFlagValues enrich :enrich async :async :as request}]
  (let [result-id (utils/gen-uuid)
        {document-plan :documentPlan data-sample-row :dataSampleRow} (get-document-plan request)
        row (if-not (str/blank? data-id) (nth (get-data data-id) (or data-sample-row 0)) (into {} (utils/key-to-keyword data-row)))
        async-result (if (nil? async) true async)]
    (if async-result
      (do
        (results/store-status result-id {:ready false})
        (results/rewrite result-id (generation-process document-plan {:sample row} reader-model enrich)) ;; TODO: this not true async. Implement it some day.
        {:status 200
         :body   {:resultId result-id}})
      {:status 200
       :body (-> (generation-process document-plan {:sample row} reader-model enrich)
                 :results
                 (first)
                 (second))})))

(defn generate-bulk [{reader-model :readerFlagValues rows :dataRows enrich :enrich :as request}]
  (let [result-id (utils/gen-uuid)
        {document-plan :documentPlan} (get-document-plan request)]
    (log/tracef "Bulk Generate request, data: %s" rows)
    (results/store-status result-id {:ready false})
    (results/rewrite result-id (generation-process document-plan (into {} (map (fn [[row-key row-values]] {row-key (into {} (utils/key-to-keyword row-values))}) rows)) reader-model enrich))
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
                         :children [{:type     "SENTENCE"
                                     :id       (utils/gen-uuid)
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
  [text lang]
  (log/debugf "Result lang: %s" lang)
  (format "%s %s" (case lang
                    "Eng" "🇬🇧"
                    "Ger" "🇩🇪"
                    "Est" "🇪🇪"
                    "Lav" "🇱🇻"
                    "Rus" "🇷🇺"
                    "🏳️") text))

(defn transform-results
  [results]
  (mapcat (fn [{:keys [enriched original lang]}]
            (if (seq enriched)
              [(prepend-lang-flag (format "📔\t%s " original) lang) (prepend-lang-flag (format "📙\t%s" enriched) lang)]
              [(prepend-lang-flag original lang)]))
          results))

(defn annotated-text-format [results]
  (->> results
       (map second)
       (flatten)                                            ;; Don't care about any bulk keys at the moment
       (transform-results)
       (wrap-to-annotated-text)))

(defn raw-format [results]
  (into {} results))

(defn standoff-format [_])                                  ;; TODO

(defn error-response [exception]
  {:status 500
   :body   {:error   true
            :message (.getMessage exception)}})

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
