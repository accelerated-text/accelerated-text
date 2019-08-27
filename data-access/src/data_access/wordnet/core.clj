(ns data-access.wordnet.core
  (:import (net.sf.extjwnl.data IndexWord PointerUtils POS Synset Word)
           (net.sf.extjwnl.dictionary Dictionary)))

(def dictionary (Dictionary/getDefaultResourceInstance))

(def wn-pos->kw-pos {POS/VERB :VB
                     POS/NOUN :NN
                     POS/ADJECTIVE :JJ
                     POS/ADVERB :RB})

(def kw-pos->wn-pos (reduce-kv (fn[m k v] (assoc m v k)) {} wn-pos->kw-pos))

(defn word->map [^Word w]
  {:lemma (.getLemma w)
   :pos (get wn-pos->kw-pos (.getPOS w))})

(defn sense->map [^Synset s]
  {:gloss (.getGloss s)
   :index (.getIndex s)
   :key (.getKey s)
   :pos (-> s .getPOS wn-pos->kw-pos)
   :words (map word->map (.getWords s))})

(defn index-word->map [^IndexWord iw]
  {:lemma (.getLemma iw)
   :pos (-> iw .getPOS wn-pos->kw-pos)
   :senses (map sense->map (.getSenses iw))})

(defn synonyms [word] (->> word :senses (mapcat :words) (set)))

(defn lookup-words
  ([word]
   (map index-word->map (-> dictionary (.lookupAllIndexWords word) (.getIndexWordCollection))))
  ([word pos]
   (some-> dictionary (.lookupIndexWord (get kw-pos->wn-pos pos) word) (index-word->map) (vector))))

(defn hyp-list-op [^IndexWord iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getDirectHypernyms))]
    (.print hypernyms)))

(defn hyp-tree-op [^IndexWord iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getHyponymTree))]
    (.print hypernyms)))

