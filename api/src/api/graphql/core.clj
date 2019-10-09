(ns api.graphql.core
  (:require [api.graphql.domain.amr :as amr-domain]
            [api.graphql.domain.data :as data-domain]
            [api.graphql.domain.dictionary :as dictionary-domain]
            [api.graphql.domain.document-plan :as document-plan-domain]
            [api.graphql.domain.thesaurus :as thesaurus-domain]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]))

(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers {:Query    {:listDataFiles   :list-data-files
                                                   :documentPlan    :document-plan
                                                   :documentPlans   :document-plans
                                                   :getDataFile     :get-data-file
                                                   :dictionary      :dictionary
                                                   :dictionaryItem  :dictionary-item
                                                   :readerFlags     :reader-flags
                                                   :concepts        :concepts
                                                   :concept         :concept
                                                   :searchThesaurus :search-thesaurus
                                                   :synonyms        :synonyms}
                                        :Mutation {:createDictionaryItem     :create-dictionary-item
                                                   :deleteDictionaryItem     :delete-dictionary-item
                                                   :updateDictionaryItem     :update-dictionary-item
                                                   :createDocumentPlan       :create-document-plan
                                                   :deleteDocumentPlan       :delete-document-plan
                                                   :updateDocumentPlan       :update-document-plan
                                                   :createPhrase             :create-phrase
                                                   :updatePhrase             :update-phrase
                                                   :deletePhrase             :delete-phrase
                                                   :updatePhraseDefaultUsage :update-phrase-default-usage
                                                   :updateReaderFlagUsage    :update-reader-flag-usage}
                                        :Concept  {:dictionaryItem :ref-dictionary-item}}})
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
                              :document-plan               document-plan-domain/get-document-plan
                              :document-plans              document-plan-domain/list-document-plans
                              :create-document-plan        document-plan-domain/add-document-plan
                              :delete-document-plan        document-plan-domain/delete-document-plan
                              :update-document-plan        document-plan-domain/update-document-plan
                              :search-thesaurus            thesaurus-domain/search-thesaurus
                              :synonyms                    thesaurus-domain/synonyms
                              :concepts                    amr-domain/list-verbclasses
                              :concept                     amr-domain/get-verbclass
                              :list-data-files             data-domain/list-data-files
                              :get-data-file               data-domain/get-data-file})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (->> (list query variables context)
       (cons nlg-schema)
       (apply execute)))
