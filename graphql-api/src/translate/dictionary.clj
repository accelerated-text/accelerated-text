(ns translate.dictionary
  (:require [clojure.tools.logging :as log]))

(defn reader-flag->schema [flag] flag)

(defn reader-flags->schema
  [flags]
  {:flags flags
   :id "???"})


(defn reader-flag-usage->schema
  [parent-id [k v]]
  (log/debugf "Got: k=%s v=%s" k v)
  (reader-flag->schema {:usage v
                        :id (format "%s/%s" parent-id (name k))
                        :flag {:name (name k)
                               :id (name k)}}))

(defn reader-flags-usage->schema
  [parent-id flags]
  (let [translate-fn (partial reader-flag-usage->schema parent-id)]
    (map translate-fn flags)))

(defn phrase->schema
  [phrase]
  (log/tracef "Phrase: %s" phrase)
  {:id (:id phrase)
   :text (:text phrase)
   :defaultUsage (-> (:flags phrase)
                     :default)
   :readerFlagUsage (reader-flags-usage->schema (:id phrase) (dissoc (:flags phrase) :default))})

(defn dictionary-item->schema
  [dict-item]
  (log/debugf "DictionaryItem: %s" dict-item)
  (log/spyf
   "Translation result %s"
   {:id (:key dict-item)
    :name (:name dict-item)
    :phrases (map phrase->schema (:phrases dict-item))
    :partOfSpeech (get dict-item :partOfSpeech "VB")}))
