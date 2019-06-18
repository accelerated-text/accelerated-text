(ns translate.dictionary)

(defn dictionary-item-in
  "From GraphQL"
  [item]
  item)

(defn dictionary-item-out
  "To GraphQL"
  [item]
  {:id (:id item)
   :name (:name item)
   :partOfSpeech nil
   :phrases (:usageModels item)})
