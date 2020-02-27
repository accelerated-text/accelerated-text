(ns api.nlg.generate
  (:require [acc-text.nlg.core :as nlg]
            [acc-text.nlp.ref-expressions :as ref-expr]
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

(defn merge-enrich-dupes [{:keys [original enriched lang] :as data}]
  (if (= original enriched)
    {:original original :lang lang}
    data))


(defn context-by-lang [context lang]
  (assoc context :dictionary (into {} (map (fn [[k v]] {k (get v lang)})
                                           (:dictionary-multilang context)))))

(defn generate-text-for-language
  [semantic-graph context enrich lang]
  (let [ref-expr-fn (partial ref-expr/apply-ref-expressions lang)
        enrich-data (into {} (map (fn [[k v]] {v (format "{%s}" (name k))}) (:data context)))
        enrich-fn (fn [text]
                    (cond-> {:original (ref-expr-fn text) :lang lang}
                      enrich (assoc :enriched (ref-expr-fn
                                               (nlg/enrich-text enrich-data text)))))]
    (->> (nlg/generate-text semantic-graph (context-by-lang context lang) lang)
         (map :text)
         (sort)
         (dedupe)
         (filter filter-empty)
         (utils/inspect-results)
         (map enrich-fn)
         (map merge-enrich-dupes))))


(defn generate-text
  ([document-plan data enrich] (generate-text document-plan data {:default true} enrich))
  ([document-plan data reader-model enrich]
   (let [languages (cond-> []
                           (get reader-model "English" false) (conj :en)
                           (get reader-model "Estonian" false) (conj :ee)
                           (get reader-model "German" false) (conj :de)
                           (get reader-model "Latvian" false) (conj :lv)
                           (get reader-model "Russian" false) (conj :ru))
         semantic-graph (parser/document-plan->semantic-graph document-plan)
         context (context/build-context semantic-graph reader-model)
         generate-fn (partial generate-text-for-language semantic-graph (assoc context :data data) enrich)]
     (log/debugf "Languages: %s" languages)
     (log/debugf "Reader Model: %s" reader-model)
     (->> languages
          (map generate-fn)
          (mapcat identity)))))

(defn generation-process [document-plan rows reader-model enrich]
  (try
    {:ready   true
     :results (doall (map (fn [[row-key data]]
                            (log/debugf "Generating result for row-key: %s, data: %s" row-key data)
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
  (format "%s %s" (case (keyword lang)
                    :en "🇬🇧"
                    :de "🇩🇪"
                    :ee "🇪🇪"
                    :lv "🇱🇻"
                    :ru "🇷🇺"
                    "🏳️") text))

(defn transform-results
  [results]
  (mapcat (fn [{:keys [enriched original lang]}]
            (if enriched
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


(def dummy-response
  {:ready   true
   :results [[:dummy [{:original (ref-expr/apply-ref-expressions :en "Test sentence one . Test sentence Two .") :lang :lv}]]
             [:dummy [{:original (ref-expr/apply-ref-expressions :en "Test sentence 12.3 one . Test sentence Two .")
                       :enriched (ref-expr/apply-ref-expressions :en "Test sentence one. Test sentence Two. Test sentence four. A very very long fith sentence test goes here. Test sentence six.")
                       :lang     :de}]]]})

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
