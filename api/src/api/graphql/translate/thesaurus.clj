(ns api.graphql.translate.thesaurus
  (:require [api.graphql.translate.concept :as translate-concept]
            [clojure.string :as string]
            [data.wordnet :as wn]))

(defn search-thesaurus [query part-of-speech]
  (let [words (mapcat wn/synonyms (if (some? part-of-speech)
                                    (wn/lookup-words query (keyword part-of-speech))
                                    (wn/lookup-words query)))]
    {:words      (map (fn [{:keys [pos lemma]}]
                        {:id           (format "%s-%s" (name pos) (string/replace lemma #" " "-"))
                         :partOfSpeech (name pos)
                         :text         lemma
                         :concept      (when (= (name pos) "VB")
                                         (translate-concept/amr->schema
                                           {:id     "PLACEHOLDER"
                                            :label  ""
                                            :roles  []
                                            :frames []}))})
                      words)
     :offset     0
     :limit      (count words)
     :totalCount (count words)}))

(defn synonyms [word-id]
  (let [[pos & tokens] (string/split word-id #"-")
        root-word (string/join " " tokens)]
    {:rootWord {:id           word-id
                :partOfSpeech pos
                :text         root-word
                :concept      (when (= pos "VB")
                                (translate-concept/amr->schema
                                  {:id     "PLACEHOLDER"
                                   :label  ""
                                   :roles  []
                                   :frames []}))}
     :synonyms (:words (search-thesaurus root-word pos))}))
