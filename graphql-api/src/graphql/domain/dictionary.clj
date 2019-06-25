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

(defn create-dictionary-item [_ arguments _]
  )

(defn delete-dictionary-item [_ arguments _]
  )

(defn reader-flags [_ _ _]
  (-> (dict-entity/list-readers)
      (translate-dict/reader-flags->schema)))

(defn reader-flag [_ arguments _]
  (-> (dict-entity/get-reader (:id arguments))
      (translate-dict/reader-flag->schema)))
