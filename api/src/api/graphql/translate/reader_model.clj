(ns api.graphql.translate.reader-model
  (:require [acc-text.nlg.dictionary.item.form :as dict-item-form]
            [api.graphql.translate.dictionary :as dict-translate]
            [clojure.tools.logging :as log]
            [data.spec.language :as lang]
            [data.entities.language :as lang-entity]))

(defn reader-flag-usage->schema [id [k v]]
  (log/debugf "Got: k=%s v=%s" k v)
  {:usage v
   :id    (format "%s/%s" id (name k))
   :flag  {:id   (name k)
           :name (name k)}})

(defn phrase->schema [{::dict-item-form/keys [id value default?] :as phrase} language]
  (log/tracef "Phrase: %s" phrase)
  {:id              id
   :text            value
   :defaultUsage    (if default? "YES" "NO")
   :readerFlagUsage (dict-translate/build-lang-user-flags language)})

(defn language->reader-flag [{::lang/keys [code name enabled?]}]
  {:id           code
   :name         name
   :defaultUsage (if (true? enabled?) "YES" "NO")})
