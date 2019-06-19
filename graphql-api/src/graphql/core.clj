(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [graphql.domain.dictionary :as dictionary-domain]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))


(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers { :Query            {:dictionary     :dictionary
                                                            :dictionaryItem :dictionary-item
                                                            }
                                        :Mutation {:updateReaderFlagUsage :update-reader-flag-usage
                                                   :updatePhraseDefaultUsage :update-phrase-usage-model
                                                   :createPhrase :create-phrase-usage-model
                                                   :deletePhrase :delete-phrase-usage-model
                                                   }
                                        :DictionaryItem   {:phrases :phrase-usage}
                                        :Phrase      {:readerFlagUsage :reader-usage}
                                        :ReaderFlagUsage  {:flag :reader-flag}
                                        }})
      (util/attach-resolvers {:dictionary                dictionary-domain/dictionary
                              :dictionary-item           dictionary-domain/dictionary-item
                              :dictionary-results        dictionary-domain/dictionary-results
                              :phrase-usage              dictionary-domain/phrase-usage-models
                              :reader-usage              dictionary-domain/reader-usage
                              :reader-flag               dictionary-domain/reader-flag
                              :update-reader-flag-usage  dictionary-domain/update-reader-flag-usage
                              :update-phrase-usage-model dictionary-domain/update-phrase-usage-model
                              :create-phrase-usage-model dictionary-domain/create-phrase-usage-model
                              :delete-phrase-usage-model dictionary-domain/delete-phrase-usage-model})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (execute nlg-schema query variables context))
