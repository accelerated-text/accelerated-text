(ns api.graphql.translate.dictionary
  (:require [api.graphql.translate.concept :as translate-concept]
            [clojure.tools.logging :as log]
            [data.utils :as utils]))

(defn reader-flag->schema [[k _]]
  {:id   (name k)
   :name (name k)})

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

(defn pos->schema [pos]
  (case pos
    :n "NN"
    :adv "RB"
    :adj "JJ"
    (name pos)))

(defn multilang-dict-item->schema [{:keys [id key pos gender language senses inflections tenses] :as dict-item}]
  (log/debugf "MultilangDictItem: %s" dict-item)
  {:id            id
   :key           key
   :pos           (pos->schema pos)
   :language      (name language)
   :gender        (case gender
                    :m  "M"
                    :f  "F"
                    :n  "N")
   :senses        (map (fn [sense] {:id (utils/gen-uuid)  :name (name sense)}) senses)
   :tenses        (map (fn [tense] {:id (:tense/id tense) :key (name (:tense/key tense)) :value (:tense/value tense)}) tenses)
   :inflections   (map (fn [inflection] {:id (:inflection/id inflection) :key (name (:inflection/key inflection)) :value (:inflection/value inflection)}) inflections)})


(defn build-lang-user-flags [translations lang]
  (map (fn [[k v]]
         {:id   (utils/gen-uuid)
          :flag {:id   k
                 :name k}
          :usage (if (= v lang)
                   "YES"
                   "NO")})
       translations))

(defn get-default-tense [tenses]
  (let [value-map (into {} (map (fn [tense] {(:tense/key tense) (:tense/value tense)}) tenses))]
    (get value-map :present "")))

(defn get-default-inflection [inflections]
  (let [value-map (into {} (map (fn [infl] {(:inflection/key infl) (:inflection/value infl)}) inflections))]
    (get value-map :nom-sg "")))


(defn multilang-dict-item->original-schema [{:keys [key pos]} items]
  (let [lang-translation {"English"    :eng
                          "German"     :ger
                          "Estonian"   :est
                          "Latvian"    :lat
                          "Lithuanian" :lit}]
    {:id           key
     :name         key
     :partOfSpeech (pos->schema pos)
     :phrases      (map (fn [{:keys [language inflections tenses]}]
                          {:defaultUsage    "YES"
                           :id              (utils/gen-uuid)
                           :readerFlagUsage (build-lang-user-flags lang-translation language)
                           :text            (cond
                                              (not-empty inflections) (get-default-inflection inflections)
                                              (not-empty tenses)       (get-default-tense tenses)
                                              :else "")})
                        items)}))

