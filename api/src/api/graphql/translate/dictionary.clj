(ns api.graphql.translate.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [api.graphql.translate.concept :as translate-concept]
            [clojure.tools.logging :as log]
            [data.utils :as utils]
            [data.entities.dictionary :as dict-entity]
            [clojure.string :as str]))

(defn reader-flag->schema [[k u]]
  {:id           (name k)
   :name         (name k)
   :defaultUsage u})

(defn reader-flags->schema [flags]
  {:flags (map reader-flag->schema (dissoc flags :default))
   :id    "default"})

(defn reader-flag-usage->schema [id [k v]]
  (log/debugf "Got: k=%s v=%s" k v)
  {:usage v
   :id    (format "%s/%s" id (name k))
   :flag  {:id   (name k)
           :name (name k)}})

(defn phrase->schema [{:keys [id text flags] :as phrase}]
  (log/tracef "Phrase: %s" phrase)
  {:id              id
   :text            text
   :defaultUsage    (:default flags)
   :readerFlagUsage (map (partial reader-flag-usage->schema id)
                         (dissoc flags :default))})

(defn dictionary-item->schema [{:keys [key name phrases] :as dict-item}]
  (log/debugf "DictionaryItem: %s" dict-item)
  (let [part-of-speech (get dict-item :partOfSpeech "V")]
    {:id           key
     :name         name
     :phrases      (map phrase->schema phrases)
     :partOfSpeech part-of-speech}))

(defn text->phrase
  ([text parent-id default-usage]
   (text->phrase text parent-id default-usage (dict-entity/get-default-flags)))
  ([text parent-id default-usage default-flags]
   {:id    (format "%s/%s" parent-id (utils/gen-uuid))
    :text  text
    :flags (assoc default-flags (dict-entity/default-language-flag) default-usage)}))

(defn schema->dictionary-item [{id :id item-name :name pos :partOfSpeech phrases :phrases}]
  (let [name-parts (str/split item-name #"_")]
    (merge
      (cond-> {}
              (< 1 (count name-parts)) (assoc ::dictionary-item/category (last name-parts))
              (< 2 (count name-parts)) (assoc ::dictionary-item/sense (last (butlast name-parts))))
      (cond-> #::dictionary-item{:key      item-name
                                 :forms    phrases
                                 :language (dict-entity/default-language)}
              (some? id) (assoc ::dictionary-item/id id)
              (some? pos) (assoc ::dictionary-item/category (name pos))))))

(defn build-lang-user-flags [lang]
  (map (fn [[flag _]]
         {:id    (utils/gen-uuid)
          :flag  {:id           flag
                  :name         flag
                  :defaultUsage (if (= (dict-entity/default-language) flag)
                                  "YES"
                                  "NO")}
          :usage (if (= (dict-entity/flag->lang flag) lang)
                   "YES"
                   "NO")})
       (dict-entity/get-default-flags)))

(defn multilang-dict-item->original-schema [{::dictionary-item/keys [id key category forms language]}]
  {:id           id
   :name         key
   :partOfSpeech category
   :phrases      (map (fn [form]
                        {:defaultUsage    "YES"
                         :id              (utils/gen-uuid)
                         :readerFlagUsage (build-lang-user-flags language)
                         :text            form})
                      forms)})
