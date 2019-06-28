(ns graphql.queries
  (:require [clojure.test :refer :all]))

(defn validate-resp
  [resp]
  (is (nil? (get resp "errors")))
  resp)

(defn create-dict-item
  [k pos]
  {:query "mutation CreateDictionaryItem($name: String!, $partOfSpeech: PartOfSpeech){createDictionaryItem(name: $name, partOfSpeech: $partOfSpeech){name}}"
   :variables {:name k, :partOfSpeech pos}})

(defn delete-dict-item
  [k]
  {:query "mutation DeleteDictionaryItem($id: ID!){deleteDictionaryItem(id: $id)}"
   :variables {:id k}})

(defn get-dict-item
  [k]
  {:query (format "{dictionaryItem(id: \"%s\"){name}}" k)})

(defn create-phrase
  [parent text default-usage]
  {:query "mutation CreatePhrase($dictionaryItemId: ID!, $text: String!, $defaultUsage: DefaultUsage){\n  createPhrase(dictionaryItemId: $dictionaryItemId, text: $text, defaultUsage: $defaultUsage){phrases{id text}}\n}"
   :variables {:dictionaryItemId parent
               :text text
               :defaultUsage default-usage}})

(defn update-phrase
  [id text]
  {:query "mutation UpdatePhrase($id: ID!, $text: String!){\n  updatePhrase(id: $id, text: $text){text defaultUsage}\n}"
   :variables {:id id
               :text text}})

(defn update-phrase-default-usage
  [id default-usage]
  {:query "mutation UpdatePhrase($id: ID!, $text: String!){\n  updatePhrase(id: $id, text: $text){text defaultUsage}\n}mutation UpdatePhraseDefaultUsage($id: ID!, $defaultUsage: DefaultUsage!){\n  updatePhraseDefaultUsage(id: $id, defaultUsage: $defaultUsage){text defaultUsage}\n}"
   :variables {:id id
               :defaultUsage default-usage}})

(defn update-reader-flag-usage
  [id usage]
  {:query "mutation UpdateReaderFlagUsage($id: ID!, $usage: Usage!){\n  updateReaderFlagUsage(id: $id, usage: $usage){id flag{id name} usage}\n}"
   :variables {:id id
               :usage usage}})
