(ns graphql.domain.dictionary
  (:require [clojure.tools.logging :as log]
            [translate.dictionary :as translate-dict]
            [translate.core :as translate-core]
            [data-access.entities.dictionary :as dict-entity]))


(defn dictionary [_ _ _]
  (->> (dict-entity/list-dictionary)
      (map translate-dict/dictionary-item->schema)
      (translate-core/paginated-response)))

(defn dictionary-item [_ arguments _]
  (log/debugf "Fetching dictionary item with args: %s" arguments)
  (-> (dict-entity/get-dictionary-item (:id arguments))
      (translate-dict/dictionary-item->schema)))

(defn create-dictionary-item [_ {:keys [name partOfSpeech]} _]
  (dict-entity/create-dictionary-item {:key name
                                       :phrases []
                                       :partOfSpeech partOfSpeech})
  (dict-entity/get-dictionary-item name))

(defn create-phrase [_ {:keys [dictionaryItemId text defaultUsage]} _]
  (log/debugf "Creating phrase: %s %s %s" dictionaryItemId text defaultUsage)
  (let [current-item (dict-entity/get-dictionary-item dictionaryItemId)
        default-flags (dict-entity/get-default-flags)
        phrases (conj (:phrases current-item) (dict-entity/text->phrase
                                               text
                                               (keyword defaultUsage)
                                               default-flags))]
    (dict-entity/update-dictionary-item {:key dictionaryItemId
                                         :phrases phrases
                                         :partOfSpeech (:partOfSpeech current-item)})))

(defn delete-dictionary-item [_ arguments _]
  )

(defn reader-flags [_ _ _]
  (-> (dict-entity/list-readers)
      (translate-dict/reader-flags->schema)))

(defn reader-flag [_ arguments _]
  (-> (dict-entity/get-reader (:id arguments))
      (translate-dict/reader-flag->schema)))
