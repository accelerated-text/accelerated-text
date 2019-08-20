(ns translate.thesaurus
  (:require [data-access.wordnet.core :as wn]
            [clojure.string :as string]))

(defn search-thesaurus [query part-of-sepeech]
  (let [words (wn/synonyms (wn/lookup-word (keyword part-of-sepeech) query))]
    {:words (map (fn [{:keys [pos lemma]}]
                   (assoc {}
                          :id (format "%s-%s"
                                      (name pos)
                                      (string/replace lemma #" " "-"))
                          :partOfSpeech (name pos)
                          :text lemma))
                 words)
     :offset 0
     :limit (count words)
     :totalCount (count words)}))

(defn synonyms [word-id]
  (let [[pos & tokens] (string/split word-id #"-")
        root-word      (string/join " " tokens)
        words          (search-thesaurus root-word pos)]
    {:rootWord {:id           word-id
                :partOfSpeech pos
                :text         root-word}
     :synonyms (:words words)}))
