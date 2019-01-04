(ns lt.tokenmill.nlg.generator.simple-nlg
  (:require [clojure.tools.logging :as log])
  (:import (simplenlg.lexicon Lexicon)
           (simplenlg.framework NLGFactory)
           (simplenlg.realiser.english Realiser)
           (simplenlg.phrasespec SPhraseSpec NPPhraseSpec VPPhraseSpec)))

(def lexicon (Lexicon/getDefaultLexicon))

(defn add-obj [^SPhraseSpec clause obj] (.setObject clause obj))
(defn add-subj [^SPhraseSpec clause subj] (.setSubject clause subj))
(defn add-verb [^SPhraseSpec clause verb] (.setVerb clause verb))
(defn add-complement [^SPhraseSpec clause complement] (.addComplement clause complement))

(defn create-noun [^NLGFactory factory str] (.createNounPhrase factory str))
(defn create-verb [^NLGFactory factory str] (.createVerbPhrase factory str))

(defn generator
  []
  (let [factory (NLGFactory. lexicon)
        realiser (Realiser. lexicon)]
    (fn [fun]
      (let [clause (.createClause factory)
            _ (fun clause)]
        (.realiseSentence realiser clause)))))
