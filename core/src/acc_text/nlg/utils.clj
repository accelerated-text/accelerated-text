(ns acc-text.nlg.utils
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log])
  (:import opennlp.ccg.grammar.Grammar
           opennlp.ccg.lexicon.LexException
           [opennlp.ccg.synsem AbstractCat AtomCat ComplexCat Sign]))

(defn str->int
  "Parses int from string"
  [s]
  (when s
    (Integer/parseInt s)))

(defn str->word
  "Creates Word instance from a simple string"
  [^Grammar grammar s]
  (when s
    (let [parsed-words (.getParsedWords grammar s)]
      (when (= 1 (count parsed-words))
        (first parsed-words)))))

(declare sign->str)
(declare sign->bracket-str)
(declare sign->debug-str)

(defn word->str
  [word]
  (.getForm word))

(defn word->sign
  "Creates Sign from Word"
  [^Grammar grammar word]
  (when word
    (try
      (let [hash  (.getSignsFromWord (.lexicon grammar) word)
            signs (.getSignsSorted hash)]
        (log/debugf "'%s' resolves into %d signs" (word->str word) (count signs))
        (log/debugf "'%s' => %s" (word->str word) (str/join "; " (map sign->debug-str signs)))
        (vec signs))
      (catch LexException e
        (log/errorf "Failed to parse word %s. Reason: %s" (word->str word) (.getMessage e))
        (vector)))))

(defn str->sign
  "Creates Sign instance from a simple string"
  [^Grammar grammar s]
  (when s
    (word->sign grammar (str->word grammar s))))

(defn sign->str
  "Returns simple string from a Sign structure"
  [sign]
  (.getOrthography sign))

(defn sign->bracket-str
  "Returns simple string from a Sign structure"
  [sign]
  (.getBracketedString sign))

(defn sign->debug-str
  [^Sign sign]
  (let [cat (.getCategory sign)]
    (format "%s[%s]" (sign->str sign) (.prettyPrint cat))))

(defn atom-cat-type [^Sign sign]
  (let [cat (.getCategory sign)]
    (if (instance? AtomCat cat)
      (.getType (cast AtomCat cat))
      false)))

(defn sentence?
  "Sign builds actual sentence"
  [^Sign sign] (= "s" (atom-cat-type sign)))

(defn partial-sentence?
  "Sign builds actual sentence"
  [^Sign sign] (contains? #{"s" "np"} (atom-cat-type sign)))

(defn conj?
  "Sign is conj, eg. 'and'"
  [^Sign sign]
  (let [cat (.getCategory sign)]
    (if (instance? ComplexCat cat)
      (let [complex (cast ComplexCat cat)
            supertag (.getSupertag complex)]
        (cond
          (= "np\\np/np" supertag) true
          (str/includes? supertag "_conj") true
          :else false))
      false)))

(defn now
  "Returns current timestamp"
  []
  (System/currentTimeMillis))

(defn clean-sentence
  "Fixes some trivial things regarding sentence"
  [sent]
  (str/replace sent #"\s,\s" ", "))
