(ns api.graphql.core
  (:require [api.graphql.domain.concept :as concept-domain]
            [api.graphql.domain.data :as data-domain]
            [api.graphql.domain.dictionary :as dictionary-domain]
            [api.graphql.domain.document-plan :as document-plan-domain]
            [api.graphql.domain.reader-model :as reader-model-domain]
            [api.graphql.domain.language :as language-domain]
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
                                                   :languages       :languages
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
                                                   :updateReaderFlagUsage    :update-reader-flag-usage
                                                   :updateLanguageUsage      :update-language-usage
                                                   :createDataFile           :create-data-file}}})
      (util/attach-resolvers {:dictionary                  #'dictionary-domain/dictionary
                              :dictionary-item             #'dictionary-domain/dictionary-item
                              :create-dictionary-item      #'dictionary-domain/create-dictionary-item
                              :delete-dictionary-item      #'dictionary-domain/delete-dictionary-item
                              :update-dictionary-item      #'dictionary-domain/update-dictionary-item
                              :create-phrase               #'dictionary-domain/create-phrase
                              :update-phrase               #'dictionary-domain/update-phrase-text
                              :delete-phrase               #'dictionary-domain/delete-phrase
                              :update-phrase-default-usage #'dictionary-domain/update-phrase-default-usage
                              :reader-flags                #'reader-model-domain/reader-model
                              :update-reader-flag-usage    #'reader-model-domain/update-reader-flag-usage
                              :languages                   #'language-domain/language-model
                              :update-language-usage       #'language-domain/update-language-usage
                              :document-plan               #'document-plan-domain/get-document-plan
                              :document-plans              #'document-plan-domain/list-document-plans
                              :create-document-plan        #'document-plan-domain/add-document-plan
                              :delete-document-plan        #'document-plan-domain/delete-document-plan
                              :update-document-plan        #'document-plan-domain/update-document-plan
                              :search-thesaurus            #'thesaurus-domain/search-thesaurus
                              :synonyms                    #'thesaurus-domain/synonyms
                              :concepts                    #'concept-domain/list-concepts
                              :concept                     #'concept-domain/get-concept
                              :list-data-files             #'data-domain/list-data-files
                              :get-data-file               #'data-domain/get-data-file
                              :create-data-file            #'data-domain/create-data-file})
      schema/compile))

(defn handle [{:keys [query variables context] :as request}]
  (log/debugf "The request is: %s" request)
  (execute nlg-schema query variables context {}))
