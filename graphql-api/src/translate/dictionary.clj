(ns translate.dictionary
  (:require [clojure.tools.logging :as log]))

(defn phrase->schema
  [phrase]
  (log/tracef "Phrase: %s" phrase)
  {:id (:id phrase)
   :text (:phrase phrase)
   :defaultUsage (:defaultUsage phrase)
   :readerFlagUsage (:readerFlagUsage phrase)})

(defn dictionary-item->schema
  [dict-item]
  (log/tracef "DictionaryItem: %s" dict-item)
  {:id (:id dict-item)
   :name (:name dict-item)
   :phrases (:usageModels dict-item)
   :partOfSpeech (get dict-item :partOfSpeech "VB")})
