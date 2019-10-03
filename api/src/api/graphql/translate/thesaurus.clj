(ns api.graphql.translate.thesaurus
  (:require [api.graphql.translate.amr :as translate-amr]
            [clojure.string :as string]
            [data.entities.amr :as amr]
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
                                         (translate-amr/verbclass->schema
                                           (amr/get-verbclass :author)))})
                      words)
     :offset     0
     :limit      (count words)
     :totalCount (count words)}))

(defn synonyms [word-id]
  (let [[pos & tokens] (string/split word-id #"-")
        root-word (string/join " " tokens)
        words (search-thesaurus root-word pos)]
    {:rootWord {:id           word-id
                :partOfSpeech pos
                :text         root-word
                :concept      (when (= pos "VB")
                                (translate-amr/verbclass->schema
                                  (amr/get-verbclass :author)))}
     :synonyms (:words words)}))
