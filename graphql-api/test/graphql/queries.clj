(ns graphql.queries)

(defn create-dict-item
  [k pos]
  {:query "mutation CreateDictionaryItem($name: String!, $partOfSpeech: PartOfSpeech){createDictionaryItem(name: $name, partOfSpeech: $partOfSpeech){name}}"
   :variables {:name k, :partOfSpeech pos}
   :operationName nil})

(defn delete-dict-item
  [k]
  {:query "mutation DeleteDictionaryItem($id: ID!){deleteDictionaryItem(id: $id)}"
   :variables {:id k}})

(defn get-dict-item
  [k]
  {:query (format "{dictionaryItem(id: \"%s\"){name}}" k)})
