(ns translate.thesaurus
  (:require [data-access.wordnet.core :as wn]
            [clojure.string :as string]))

(defn search-thesaurus [query part-of-speech]
  (let [words (mapcat wn/synonyms (if (some? part-of-speech)
                                    (wn/lookup-words query (keyword part-of-speech))
                                    (wn/lookup-words query)))]
    {:words      (map (fn [{:keys [pos lemma]}]
                        {:id           (format "%s-%s" (name pos) (string/replace lemma #" " "-"))
                         :partOfSpeech (name pos)
                         :text         lemma})
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
                :text         root-word}
     :synonyms (:words words)}))
