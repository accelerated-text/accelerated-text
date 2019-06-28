(ns nlg.generator.simple-nlg
  (:import (simplenlg.lexicon Lexicon)
           (simplenlg.framework NLGFactory CoordinatedPhraseElement)
           (simplenlg.realiser.english Realiser)
           (simplenlg.phrasespec SPhraseSpec NPPhraseSpec VPPhraseSpec)))

(def lexicon (Lexicon/getDefaultLexicon))

(defn add-obj [^SPhraseSpec clause obj] (if obj (.setObject clause obj)))
(defn add-subj [^SPhraseSpec clause subj] (if subj (.setSubject clause subj)))
(defn add-verb [^SPhraseSpec clause verb] (if verb (.setVerb clause verb)))
(defn add-complement [^SPhraseSpec clause complement] (if complement (.addComplement clause complement)))

(defn add-adverb
  [^NPPhraseSpec noun adverb]
  (.addPreModifier noun adverb)
  noun)

(defn create-noun
  ([^NLGFactory factory noun]
   (.createNounPhrase factory noun))
  ([^NLGFactory factory specifier noun]
   (.createNounPhrase factory specifier noun)))

(defn create-multi-nouns
  [^NLGFactory factory adverb args]
  (let [nouns (map #(create-noun factory %) args)
        head (if adverb (add-adverb (first nouns) adverb) (first nouns))
        tail (rest nouns)]
    (cons head tail)))

(defn create-verb [^NLGFactory factory verb] (.createVerbPhrase factory verb))
(defn create-adverb [^NLGFactory factory adverb] (.createAdverbPhrase factory adverb))

(defn concat-multi [^NLGFactory factory elements]
  (let [coordinated (.createCoordinatedPhrase factory)]
    (doseq [e elements] (.addCoordinate coordinated e))
    coordinated))

(defn generator
  []
  (let [factory (NLGFactory. lexicon)
        realiser (Realiser. lexicon)]
    (fn [fun]
      (let [clause (.createClause factory)
            _ (fun clause factory)]
        (.realiseSentence realiser clause)))))
