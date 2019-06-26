(ns graphql.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [graphql.domain.dictionary :as dictionary-domain]
            [graphql.domain.document-plan :as dp-domain]
            [translate.core :as translate]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.parser.schema :as parser]
            [com.walmartlabs.lacinia :refer [execute]]))


(def nlg-schema
  (-> "schema.graphql"
      (io/resource)
      slurp
      (parser/parse-schema {:resolvers {:Query    {:dictionary     :dictionary
                                                   :dictionaryItem :dictionary-item
                                                   :documentPlan   :document-plan
                                                   :documentPlans  :document-plans
                                                   :readerFlags    :reader-flags}
                                        :Mutation {:createDictionaryItem :create-dictionary-item
                                                   :createPhrase :create-phrase
                                                   :deleteDictionaryItem :delete-dictionary-item
                                                   }
                                        }})
      (util/attach-resolvers {:dictionary                dictionary-domain/dictionary
                              :dictionary-item           dictionary-domain/dictionary-item
                              :create-dictionary-item    dictionary-domain/create-dictionary-item
                              :delete-dictionary-item    dictionary-domain/delete-dictionary-item
                              :create-phrase             dictionary-domain/create-phrase
                              :reader-flags              dictionary-domain/reader-flags
                              :document-plan             dp-domain/document-plan
                              :document-plans            dp-domain/document-plans})
      schema/compile))

(defn nlg [{:keys [query variables context] :as request}]
  (log/infof "The request is: %s" request)
  (->> (translate/translate-input query variables context)
       (cons nlg-schema)
       (apply execute)))
