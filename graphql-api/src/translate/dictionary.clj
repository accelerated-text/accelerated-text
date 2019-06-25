(ns translate.dictionary
  (:require [clojure.tools.logging :as log]))

(defn reader-flag->schema [flag] flag)

(defn reader-flags->schema
  [flags]
  {:flags flags
   :id "???"})

(defn phrase->schema
  [phrase]
  (log/tracef "Phrase: %s" phrase)
  {:id (:id phrase)
   :text (:phrase phrase)
   :defaultUsage (:defaultUsage phrase)
   :readerFlagUsage (:readerFlagUsage phrase)})

(defn dictionary-item->schema
  [dict-item]
  (log/debugf "DictionaryItem: %s" dict-item)
  {:id (:key dict-item)
   :name (:key dict-item)
   :phrases (:phrases dict-item)
   :partOfSpeech (get dict-item :partOfSpeech "VB")})
