(ns graphql.domain.thesaurus
  (:require [translate.thesaurus :as translate]))

(defn search-thesaurus [_ {:keys [query partOfSpeech]} _]
  (translate/search-thesaurus query partOfSpeech))

(defn synonyms [_ {:keys [wordId]} _]
  (translate/synonyms wordId))
