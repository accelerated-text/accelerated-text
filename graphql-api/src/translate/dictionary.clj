(ns translate.dictionary
  (:require [clojure.tools.logging :as log]))

(defn reader-flag->schema [flag] flag)

(defn reader-flags->schema
  [flags]
  {:flags flags
   :id "???"})

(defn reader-flag-usage->schema
  [parent-id flags]
  (->> (map (fn [[k v]] {:usage v
                         :id parent-id
                         :flag {:name (name k)
                               :id (format "%s/%s" parent-id (name k))}})
            flags)
       (map reader-flag->schema)))

(defn phrase->schema
  [phrase]
  (log/tracef "Phrase: %s" phrase)
  {:id (:id phrase)
   :text (:text phrase)
   :defaultUsage (-> (:flags phrase)
                     :default)
   :readerFlagUsage (reader-flag-usage->schema (:id phrase) (:flags phrase))})

(defn dictionary-item->schema
  [dict-item]
  (log/debugf "DictionaryItem: %s" dict-item)
  (log/spyf
   "Translation result %s"
   {:id (:key dict-item)
    :name (:key dict-item)
    :phrases (map phrase->schema (:phrases dict-item))
    :partOfSpeech (get dict-item :partOfSpeech "VB")}))
