(ns api.graphql.translate.dictionary
  (:require [api.graphql.translate.concept :as translate-concept]
            [clojure.tools.logging :as log]
            [data.utils :as utils]
            [data.entities.dictionary :as dict-entity]))

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
  (let [part-of-speech (get dict-item :partOfSpeech "VB")]
    {:id           key
     :name         name
     :phrases      (map phrase->schema phrases)
     :partOfSpeech part-of-speech
     :concept      (when (= part-of-speech "VB")
                     (translate-concept/amr->schema
                       {:id     "PLACEHOLDER"
                        :label  ""
                        :roles  []
                        :frames []}))}))

(defn multilang-dict-item->schema [{:keys [id key pos gender language senses inflections tenses] :as dict-item}]
  (log/debugf "MultilangDictItem: %s" dict-item)
  {:id            id
   :key           key
   :pos           pos
   :language      (name language)
   :gender        (get {:m "M" :f "F" :n "N"} gender)
   :senses        (map (fn [sense] {:id (utils/gen-uuid)  :name (name sense)}) senses)
   :tenses        (map (fn [tense] {:id (:tense/id tense) :key (name (:tense/key tense)) :value (:tense/value tense)}) tenses)
   :inflections   (map (fn [inflection] {:id (:inflection/id inflection) :key (name (:inflection/key inflection)) :value (:inflection/value inflection)}) inflections)})

(defn build-lang-user-flags [lang]
  (map (fn [[k _]]
         {:id   (utils/gen-uuid)
          :flag {:id   (name k)
                 :name (name k)}
          :usage (if (= (subs (name k) 0 3) (name lang))
                   "YES"
                   "NO")})
       (dict-entity/get-default-flags)))

(defn get-default-tense [tenses]
  (let [value-map (into {} (map (fn [tense] {(:tense/key tense) (:tense/value tense)}) tenses))]
    (get value-map :present-tense "")))

(defn get-default-inflection [inflections]
  (let [value-map (into {} (map (fn [infl] {(:inflection/key infl) (:inflection/value infl)}) inflections))]
    (get value-map :nom-sg "")))

(defn multilang-dict-item->original-schema [{:keys [key pos]} items]
  {:id           key
   :name         key
   :partOfSpeech pos
   :phrases      (map (fn [{:keys [language inflections tenses]}]
                        {:defaultUsage    "YES"
                         :id              (utils/gen-uuid)
                         :readerFlagUsage (build-lang-user-flags language)
                         :text            (cond
                                            (not-empty inflections) (get-default-inflection inflections)
                                            (not-empty tenses)       (get-default-tense tenses)
                                            :else "")})
                      items)})
