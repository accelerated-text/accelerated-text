(ns api.graphql.domain.thesaurus
  (:require [com.walmartlabs.lacinia.resolve :refer [resolve-as]]))

;; TODO: thesaurus implementation

(defn search-thesaurus [_ _ _]
  (resolve-as {:words      []
               :offset     0
               :limit      0
               :totalCount 0}))

(defn synonyms [_ {:keys [wordId]} _]
  (resolve-as {:rootWord wordId
               :synonyms []}))
