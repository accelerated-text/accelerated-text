(ns api.nlg.core
  (:require [acc-text.nlg.core :as nlg]
            [acc-text.nlp.utils :as nlp]
            [acc-text.nlg.semantic-graph.utils :refer [get-dictionary-keys]]
            [api.nlg.ref-expr :refer [enable-ref-expr? apply-ref-expressions]]
            [api.nlg.enrich :refer [enable-enrich? enrich]]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [api.utils :as utils]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [data.entities.amr :refer [find-amrs]]
            [data.entities.dictionary :refer [build-dictionaries]]
            [data.entities.results :as results]
            [data.entities.reader-model :refer [available-reader-model]]
            [data.spec.reader-model :as reader-model]
            [data.spec.result :as result]
            [data.spec.result.annotation :as annotation]
            [data.spec.result.row :as row]))

(defn remove-duplicates? []
  (Boolean/valueOf ^String (or (System/getenv "REMOVE_DUPLICATES") "TRUE")))

(defn deduplicate [results]
  (map first (vals (group-by :text results))))

(defn enable-cache? []
  (Boolean/valueOf ^String (or (System/getenv "ENABLE_CACHE") "TRUE")))

(defn with-cache [request-hash {id ::result/id :as result}]
  (when (enable-cache?)
    (do
      (log/debugf "Caching result `%s`" id)
      (results/write-cached-result request-hash id)))
  result)

(defn add-annotations [{text ::row/text :as row}]
  (assoc row ::row/annotations (mapv (fn [{:keys [idx text]}]
                                       #::annotation{:id   (utils/gen-uuid)
                                                     :idx  idx
                                                     :text text})
                                     (nlp/annotate text))))

(defn ->result-row [{:keys [text language readers enriched?]}]
  #::row{:id        (utils/gen-uuid)
         :language  language
         :readers   readers
         :enriched? (true? enriched?)
         :text      (cond->> text (enable-ref-expr?) (apply-ref-expressions language))})

(defn select-enabled-readers [reader-model]
  (let [{:keys [language reader]} (->> reader-model (filter ::reader-model/enabled?) (group-by ::reader-model/type))]
    {:languages (set (map ::reader-model/code language))
     :readers   (set (map ::reader-model/code reader))}))

(defn generate-text
  [{:keys [id document-plan data reader-model] :or {id (utils/gen-uuid) data {} reader-model (available-reader-model)}}]
  (let [{:keys [languages readers]} (select-enabled-readers reader-model)
        semantic-graph (document-plan->semantic-graph document-plan)
        amrs (find-amrs semantic-graph)
        semantic-graphs (cons semantic-graph amrs)
        dictionary-keys (set (concat (vals data) (mapcat get-dictionary-keys semantic-graphs)))
        context {:amr        amrs
                 :data       data
                 :readers    readers
                 :dictionary (build-dictionaries dictionary-keys languages)}
        request-hash (hash [semantic-graph context languages])]
    (try
      (if-let [cached-result (when (enable-cache?) (results/fetch-cached-result request-hash))]
        (do
          (log/infof "Found cached result `%s`" (::result/id cached-result))
          (assoc cached-result ::result/id id))
        (with-cache
          request-hash
          #::result{:id     id
                    :status :ready
                    :rows   (transduce
                              (comp
                                (mapcat (fn [lang]
                                          (let [context (update context :dictionary #(get % lang []))]
                                            (cond-> (nlg/generate-text semantic-graph context lang)
                                                    (and (= "Eng" lang) (enable-enrich?)) (enrich data)
                                                    (remove-duplicates?) (deduplicate)))))
                                (remove #(str/blank? (:text %)))
                                (map ->result-row)
                                (map add-annotations))
                              conj
                              languages)}))
      (catch Exception e
        (log/error (.getMessage e))
        (log/trace (str/join "\n" (.getStackTrace e)))
        #::result{:id            id
                  :status        :error
                  :error-message (or (.getMessage e) "")}))))
