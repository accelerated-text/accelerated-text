(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [nlg.dictionary :as dictionary]
            [translate.dictionary :as translate-dict]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn dictionary [_ _ _]
  (dictionary/list-dictionary-items))

(defn dictionary-item [_ arguments _]
  (-> (dictionary/dictionary-item arguments)
      (translate-dict/dictionary-item-out)))

(defn dictionary-results [_ arguments _]
  {:items (list (dictionary-item arguments))
   :offset 0
   :limit 0
   :totalCount 0})

(defn phrase-usage-models [_ _ value]
  (:phrase-usage-model (dictionary/phrase-usage-models {:ids (:usageModels value)})))

(defn reader-usage [_ _ value]
  (:reader-flag-usage (dictionary/reader-flag-usages {:ids (:readerUsage value)})))

(defn reader-flag [_ _ value]
  (dictionary/reader-flag {:id (:readerFlag value)}))

(defn update-reader-flag-usage [_ arguments _]
  (dictionary/update-reader-flag-usage arguments))

(defn update-phrase-usage-model [_ arguments _]
  (dictionary/update-phrase-usage arguments))

(defn create-phrase-usage-model [_ {:keys [dictionaryItemId phrase defaultUsage] :or {defaultUsage :YES}} _]
  (let [usage-models (:usageModels (dictionary/dictionary-item {:id dictionaryItemId}))
        new-phrase-model-id (:id (dictionary/create-phrase-usage-model {:phrase phrase :defaultUsage defaultUsage}))]
    (dictionary/update-dictionary-item-usage-models {:id dictionaryItemId :phraseUsage (conj usage-models new-phrase-model-id)})))

(defn delete-phrase-usage-model [_ {:keys [id]} _]
  (let [dictionary-id (dictionary/dictionary-item-id-that-contains-phrase-model {:id id})
        usage-models (:phraseUsage (dictionary/dictionary-item {:id dictionary-id}))]
    (dictionary/delete-phrase-usage-model {:id id})
    (dictionary/update-dictionary-item-usage-models {:id dictionary-id :phraseUsage (remove #(= id %) usage-models)})))

(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers { :Query            {:dictionary     :dictionary
                                                            ;; :dictionary-item :dictionary-item
                                                            }
                                        ;; :Mutation         {:updateReaderFlagUsage :update-reader-flag-usage
                                        ;;                    :updatePhraseUsageDefault :update-phrase-usage-model
                                        ;;                    :createPhraseUsage :create-phrase-usage-model
                                        ;;                    :deletePhraseUsage :delete-phrase-usage-model}
                                        ;; :DictionaryItem   {:phraseUsage :phrase-usage}
                                        ;; :PhraseUsage      {:readerUsage :reader-usage}
                                        ;; :ReaderFlagUsage  {:flag :reader-flag}
                                        }})
      (util/attach-resolvers {:dictionary                dictionary
                              :dictionary-item           dictionary-item
                              :dictionary-results        dictionary-results
                              :phrase-usage              phrase-usage-models
                              :reader-usage              reader-usage
                              :reader-flag               reader-flag
                              :update-reader-flag-usage  update-reader-flag-usage
                              :update-phrase-usage-model update-phrase-usage-model
                              :create-phrase-usage-model create-phrase-usage-model
                              :delete-phrase-usage-model delete-phrase-usage-model})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (execute nlg-schema query variables context))
