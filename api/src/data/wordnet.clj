(ns data.wordnet
  (:require [clojure.tools.logging :as log])
  (:import (net.sf.extjwnl.data IndexWord PointerUtils POS Synset Word)
           (net.sf.extjwnl.dictionary Dictionary)))

(def dictionary (Dictionary/getDefaultResourceInstance))

(def wn-pos->gf-pos
  {POS/VERB      "V"
   POS/NOUN      "N"
   POS/ADVERB    "Adv"
   POS/ADJECTIVE "A"})

(defn gf-pos->wn-pos [pos]
  (case pos
    "V" POS/VERB
    "VV" POS/VERB
    "VS" POS/VERB
    "VQ" POS/VERB
    "VP" POS/VERB
    "VA" POS/VERB
    "V2V" POS/VERB
    "V2S" POS/VERB
    "V2A" POS/VERB
    "V2Q" POS/VERB
    "V2" POS/VERB
    "V3" POS/VERB
    "V0" POS/VERB
    "N" POS/NOUN
    "N2" POS/NOUN
    "N3" POS/NOUN
    "PN" POS/NOUN
    "Adv" POS/ADVERB
    "A" POS/ADJECTIVE
    "A2" POS/ADJECTIVE
    (log/warnf "Received unknown POS tag in thesaurus search: `%s`" pos)))

(defn word->map [^Word w]
  {:lemma (.getLemma w)
   :pos   (get wn-pos->gf-pos (.getPOS w))})

(defn sense->map [^Synset s]
  {:gloss (.getGloss s)
   :index (.getIndex s)
   :key   (.getKey s)
   :pos   (-> s .getPOS wn-pos->gf-pos)
   :words (map word->map (.getWords s))})

(defn index-word->map [^IndexWord iw]
  {:lemma  (.getLemma iw)
   :pos    (-> iw .getPOS wn-pos->gf-pos)
   :senses (map sense->map (.getSenses iw))})

(defn synonyms [word] (->> word :senses (mapcat :words) (distinct)))

(defn lookup-words
  ([word]
   (map index-word->map (-> dictionary (.lookupAllIndexWords word) (.getIndexWordCollection))))
  ([word pos]
   (if-let [wn-pos (gf-pos->wn-pos pos)]
     (some-> dictionary (.lookupIndexWord wn-pos word) (index-word->map) (vector))
     (lookup-words word))))

(defn hyp-list-op [^IndexWord iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getDirectHypernyms))]
    (.print hypernyms)))

(defn hyp-tree-op [^IndexWord iw]
  (let [hypernyms (-> iw .getSenses (.get 0) (PointerUtils/getHyponymTree))]
    (.print hypernyms)))
