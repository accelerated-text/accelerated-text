(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [nlg.lexicon :as lexicon]
            [nlg.dictionary :as dictionary]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn lexicon [context arguments value]
  (let [result (:body (lexicon/search arguments nil))]
    (update result :items #(map (fn [{:keys [key] :as item}] (assoc item :id key)) %))))

(defn dictionary [_ _ _]
  (dictionary/list-dictionary-items))

(defn dictionary-item [_ arguments _]
  (dictionary/dictionary-item arguments))

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
    (dictionary/update-dictionary-item-usage-models {:id dictionaryItemId :usageModels (conj usage-models new-phrase-model-id)})))

(defn delete-phrase-usage-model [_ {:keys [id]} _]
  (let [dictionary-id (dictionary/dictionary-item-id-that-contains-phrase-model {:id id})
        usage-models (:usageModels (dictionary/dictionary-item {:id dictionary-id}))]
    (dictionary/delete-phrase-usage-model {:id id})
    (dictionary/update-dictionary-item-usage-models {:id dictionary-id :usageModels (remove #(= id %) usage-models)})))

(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers {:Query            {:searchLexicon  :lexicon
                                                           :dictionary     :dictionary
                                                           :dictionaryItem :dictionary-item}
                                        :Mutation         {:updateReaderFlagUsage :update-reader-flag-usage
                                                           :updatePhraseUsageModelDefault :update-phrase-usage-model
                                                           :createPhraseUsageModel :create-phrase-usage-model
                                                           :deletePhraseUsageModel :delete-phrase-usage-model}
                                        :DictionaryItem   {:usageModels :phrase-usage}
                                        :PhraseUsageModel {:readerUsage :reader-usage}
                                        :ReaderFlagUsage  {:flag :reader-flag}}})
      (util/attach-resolvers {:lexicon                   lexicon
                              :dictionary                dictionary
                              :dictionary-item           dictionary-item
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
