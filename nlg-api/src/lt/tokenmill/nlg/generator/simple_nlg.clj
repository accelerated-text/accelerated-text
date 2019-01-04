(ns lt.tokenmill.nlg.generator.simple-nlg
  (:require [clojure.tools.logging :as log])
  (:import (simplenlg.lexicon Lexicon)
           (simplenlg.framework NLGFactory CoordinatedPhraseElement)
           (simplenlg.realiser.english Realiser)
           (simplenlg.phrasespec SPhraseSpec NPPhraseSpec VPPhraseSpec)))

(def lexicon (Lexicon/getDefaultLexicon))

(defn add-obj [^SPhraseSpec clause obj] (.setObject clause obj))
(defn add-subj [^SPhraseSpec clause subj] (.setSubject clause subj))
(defn add-verb [^SPhraseSpec clause verb] (.setVerb clause verb))
(defn add-complement [^SPhraseSpec clause complement] (.addComplement clause complement))

(defn create-noun
  ([^NLGFactory factory noun]
   (.createNounPhrase factory noun))
   ([^NLGFactory factory specifier noun]
    (.createNounPhrase factory specifier noun)))
  
(defn create-verb [^NLGFactory factory verb] (.createVerbPhrase factory verb))
(defn create-adverb [^NLGFactory factory adverb] (.createAdverbPhrase factory adverb))

(defn concat-multi [^NLGFactory factory elements]
  (let [coordinated (.createCoordinatedPhrase factory)]
    (do
      (doseq [e elements] (.addCoordinate coordinated e))
      coordinated)))
    

(defn generator
  []
  (let [factory (NLGFactory. lexicon)
        realiser (Realiser. lexicon)]
    (fn [fun]
      (let [clause (.createClause factory)
            _ (fun clause factory)]
        (.realiseSentence realiser clause)))))
