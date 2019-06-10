(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [nlg.lexicon :as lexicon]
            [nlg.dictionary :as dictionary]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))

(defn get-lexicon [context arguments value]
  (let [result (:body (lexicon/search arguments nil))]
    (update result :items #(map (fn [{:keys [key] :as item}] (assoc item :id key)) %))))

(defn get-dictionary [_ _ _]
  (dictionary/list-dictionary-items))

(defn get-dictionary-item [_ arguments _]
  (dictionary/dictionary-item arguments))

(defn get-phrase-usage-models [_ _ value]
  (:phrase-usage-model (dictionary/phrase-usage-models {:ids (:usageModels value)})))

(defn get-reader-usage [_ _ value]
  (:reader-flag-usage (dictionary/reader-flag-usages {:ids (:readerUsage value)})))

(defn get-reader-flag [_ _ value]
  (dictionary/reader-flag {:id (:readerFlag value)}))

(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers {:Query            {:searchLexicon  :get-lexicon
                                                           :dictionary     :dictionary
                                                           :dictionaryItem :dictionary-item}
                                        :DictionaryItem   {:usageModels :phrase-usage}
                                        :PhraseUsageModel {:readerUsage :get-reader-usage}
                                        :ReaderFlagUsage  {:flag :get-reader-flag}}})
      (util/attach-resolvers {:get-lexicon      get-lexicon
                              :dictionary       get-dictionary
                              :dictionary-item  get-dictionary-item
                              :phrase-usage     get-phrase-usage-models
                              :get-reader-usage get-reader-usage
                              :get-reader-flag  get-reader-flag})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (execute nlg-schema query variables context))
