(ns data-access.wordnet.core
  (:import [net.sf.extjwnl.data PointerUtils POS]
           net.sf.extjwnl.dictionary.Dictionary))

(def dictionary (Dictionary/getDefaultResourceInstance))

(def wn-pos->kw-pos {POS/VERB :VB
                     POS/NOUN :NN
                     POS/ADJECTIVE :JJ
                     POS/ADVERB :RB})

(def kw-pos->wn-pos (reduce-kv (fn[m k v] (assoc m v k)) {} wn-pos->kw-pos))

(defn word->map [word]
  {:lemma (.getLemma word)
   :pos (get wn-pos->kw-pos (.getPOS word))})

(defn sense->map [sense]
  {:gloss (.getGloss sense)
   :index (.getIndex sense)
   :key (.getKey sense)
   :pos (-> sense .getPOS wn-pos->kw-pos)
   :words (map word->map (.getWords sense))})

(defn index-word->map [iw]
  {:lemma (.getLemma iw)
   :pos (.getPOS iw)
   :synset-offsets (.getSynsetOffsets iw)
   :senses (map sense->map (.getSenses iw))})

(defn synonyms [word] (->> word :senses (mapcat :words) (set)))

(defn lookup-word [pos word] (index-word->map (.lookupIndexWord dictionary (get kw-pos->wn-pos pos) word)))

(defn lookup-words [word] (-> dictionary (.lookupAllIndexWords word) (.getIndexWordCollection) (map index-word->map)))

(defn hyp-list-op [iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getDirectHypernyms))]
    (.print hypernyms)))

(defn hyp-tree-op [iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getHyponymTree))]
    (.print hypernyms)))

