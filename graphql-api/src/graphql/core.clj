(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [graphql.domain.dictionary :as dictionary-domain]
            [graphql.domain.amr :as amr-domain]
            [graphql.domain.thesaurus :as thesaurus-domain]
            [translate.core :as translate]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))


(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers {:Query    {:dictionary      :dictionary
                                                   :dictionaryItem  :dictionary-item
                                                   :readerFlags     :reader-flags
                                                   :concepts        :concepts
                                                   :concept         :concept
                                                   :searchThesaurus :search-thesaurus
                                                   :synonyms        :synonyms}
                                        :Mutation {:createDictionaryItem     :create-dictionary-item
                                                   :deleteDictionaryItem     :delete-dictionary-item
                                                   :updateDictionaryItem     :update-dictionary-item
                                                   :createPhrase             :create-phrase
                                                   :updatePhrase             :update-phrase
                                                   :deletePhrase             :delete-phrase
                                                   :updatePhraseDefaultUsage :update-phrase-default-usage
                                                   :updateReaderFlagUsage    :update-reader-flag-usage
                                                   }
                                        :Concept  {:dictionaryItem :ref-dictionary-item}
                                        }})
      (util/attach-resolvers {:dictionary                  dictionary-domain/dictionary
                              :ref-dictionary-item         dictionary-domain/ref-dictionary-item
                              :dictionary-item             dictionary-domain/dictionary-item
                              :create-dictionary-item      dictionary-domain/create-dictionary-item
                              :delete-dictionary-item      dictionary-domain/delete-dictionary-item
                              :update-dictionary-item      dictionary-domain/update-dictionary-item
                              :create-phrase               dictionary-domain/create-phrase
                              :update-phrase               dictionary-domain/update-phrase-text
                              :delete-phrase               dictionary-domain/delete-phrase
                              :update-phrase-default-usage dictionary-domain/update-phrase-default-usage
                              :update-reader-flag-usage    dictionary-domain/update-reader-flag-usage
                              :reader-flags                dictionary-domain/reader-flags
                              :search-thesaurus            thesaurus-domain/search-thesaurus
                              :synonyms                    thesaurus-domain/synonyms

                              :concepts amr-domain/list-verbclasses
                              :concept  amr-domain/get-verbclass})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (->> (translate/translate-input query variables context)
       (cons nlg-schema)
       (apply execute)))
