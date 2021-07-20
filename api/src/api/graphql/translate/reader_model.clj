(ns api.graphql.translate.reader-model
  (:require [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [api.graphql.translate.dictionary :as dict-translate]
            [clojure.tools.logging :as log]
            [data.spec.reader-model :as reader-model]))

(defn reader-flag-usage->schema [id [k v]]
  (log/debugf "Got: k=%s v=%s" k v)
  {:usage v
   :id    (format "%s/%s" id (name k))
   :flag  {:id   (name k)
           :name (name k)}})

(defn phrase->schema [{::dict-item-form/keys [id value default?] :as phrase} language group-id]
  (log/tracef "Phrase: %s" phrase)
  {:id              id
   :text            value
   :defaultUsage    (if default? "YES" "NO")
   :readerFlagUsage (dict-translate/build-reader-model-user-flags language group-id)})

(defn reader-model->reader-flag [{::reader-model/keys [code name enabled?]}]
  {:id           code
   :name         name
   :defaultUsage (if (true? enabled?) "YES" "NO")})
