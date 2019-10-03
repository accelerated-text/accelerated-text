(ns api.graphql.domain.thesaurus
  (:require [api.graphql.translate.thesaurus :as translate]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]))

(defn search-thesaurus [_ {:keys [query partOfSpeech]} _]
  (resolve-as (translate/search-thesaurus query partOfSpeech)))

(defn synonyms [_ {:keys [wordId]} _]
  (resolve-as (translate/synonyms wordId)))
