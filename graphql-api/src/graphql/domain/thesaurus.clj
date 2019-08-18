(ns graphql.domain.thesaurus)

(defn create-dictionary-item [_ {:keys [query partOfSpeech]} _]
  {:words [{:id "X"
            :partOfSpeech "VB"
            :text "run"}]
   :offset 0
   :limit 11
   :totalCount 4})
