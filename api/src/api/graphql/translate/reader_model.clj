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

(defn reader-model->reader-flag [{::reader-model/keys [code name flag enabled?]}]
  {:id           code
   :name         name
   :flag         flag
   :defaultUsage (if (true? enabled?) "YES" "NO")})

(defn get-language-flag [code]
  (case (keyword code)
    :Eng "🇬🇧"
    :Ger "🇩🇪"
    :Est "🇪🇪"
    :Lav "🇱🇻"
    :Lit "🇱🇹"
    :Rus "🇷🇺"
    :Spa "🇪🇸"
    "🏳️"))

(defn reader-flag->reader-model [type {:keys [id flag defaultUsage] :as args}]
  #::reader-model{:code       (name id)
                  :name       (:name args)
                  :flag       (if (and (= :language type) (nil? flag))
                                (get-language-flag id)
                                flag)
                  :type       type
                  :enabled?   (= :YES defaultUsage)
                  :available? true})
