(ns graphql.domain.wordnet
  (:import [net.sf.extjwnl.data PointerUtils POS]
           net.sf.extjwnl.dictionary.Dictionary))

(def dictionary (Dictionary/getDefaultResourceInstance))

(defn word->map [word] word)

(defn sense->map [sense]
  {:gloss (.getGloss sense)
   :index (.getIndex sense)
   :key (.getKey sense)
   :pos (.getPOS sense)
   :words (map word->map (.getWords sense))
   })

(defn index-word->map [iw]
  {:lemma (.getLemma iw)
   :pos (.getPOS iw)
   :synset-offsets (.getSynsetOffsets iw)
   :senses (.getSenses iw)}
  )

(defn lookup-word [pos word] (.lookupIndexWord dictionary pos word))

(defn lookup-words [word] (-> dictionary (.lookupAllIndexWords word) (.getIndexWordCollection) (seq)))

(defn hyp-list-op [iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getDirectHypernyms))]
    (.print hypernyms)))

(defn hyp-tree-op [iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getHyponymTree))]
    (.print hypernyms)))
