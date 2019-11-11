(ns api.graphql.translate.dictionary
  (:require [api.graphql.translate.concept :as translate-concept]
            [clojure.tools.logging :as log]
            [data.entities.amr :as amr]))

(defn reader-flag->schema [flag]
  flag)

(defn reader-flags->schema [flags]
  {:flags flags
   :id    "???"})

(defn reader-flag-usage->schema [id [k v]]
  (log/debugf "Got: k=%s v=%s" k v)
  (reader-flag->schema {:usage v
                        :id    (format "%s/%s" id (name k))
                        :flag  {:name (name k)
                                :id   (name k)}}))

(defn phrase->schema [{:keys [id text flags] :as phrase}]
  (log/tracef "Phrase: %s" phrase)
  {:id              id
   :text            text
   :defaultUsage    (:default flags)
   :readerFlagUsage (map (partial reader-flag-usage->schema id)
                         (dissoc flags :default))})

(defn dictionary-item->schema [{:keys [key name phrases] :as dict-item}]
  (log/debugf "DictionaryItem: %s" dict-item)
  (let [part-of-speech (get dict-item :partOfSpeech "VB")]
    {:id           key
     :name         name
     :phrases      (map phrase->schema phrases)
     :partOfSpeech part-of-speech
     :concept      (when (= part-of-speech "VB")
                     (translate-concept/amr->schema
                       (amr/load-single :author)))}))
