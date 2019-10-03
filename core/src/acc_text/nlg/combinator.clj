(ns acc-text.nlg.combinator
  (:require [acc-text.nlg.utils :as utils]
            [clojure.set :refer [difference]]
            [clojure.tools.logging :as log])
  (:import [opennlp.ccg.grammar Grammar RuleGroup]
           opennlp.ccg.realize.Realizer
           [opennlp.ccg.synsem AtomCat Category Sign]
           [opennlp.ccg.lexicon Word]
           opennlp.ccg.unify.UnifyControl))

(defn apply-rules
  "Apply binary rules"
  [^RuleGroup rules ^Sign left ^Sign right]
  (log/tracef "Applying binary rule on: %s -> %s" (utils/sign->bracket-str left) (utils/sign->bracket-str right))
  (let [results (.applyBinaryRules rules left right)]
    (when (seq results)
      (log/debugf "Applying binary rule on: '%s' -> '%s' Got: '%s'" (utils/sign->bracket-str left) (utils/sign->bracket-str right) results)
      (when (utils/conj? left)
        (log/tracef "Reindexing: %s" right)
        (UnifyControl/reindex (.getCategory right))))
    results))

(defn items->combinations
  "Give sign and apply it to array of signs"
  [^RuleGroup rules pairs]
  (flatten (mapcat identity (map (fn [[left right]] (apply-rules rules left right)) pairs))))

(defn filter-implicit [word]
  (contains? #{"IN" "P" "LX" "Conj" "A"} (.getPOS word)))

(defn extract-words
  [group-map]
  (let [keys (.keySet group-map) ;; this is actually a Set of `Word`, but these are surface words (no POS, stem etc)
        ]
    (flatten
     (map
      (fn [k] (map #(.getWord %) (.get group-map k)))
      keys))))

(defn additional-signs
  "Additional words to be included when building sentence"
  [^Grammar grammar]
  (let [words (list "and" "," "." "the" "is")
        word-fn (partial utils/word->sign grammar)
        implicit (->> (.lexicon grammar)
                      (.getWords)
                      (extract-words)
                      (filter filter-implicit)
                      (map word-fn)
                      (flatten))
        sign-fn (partial utils/str->sign grammar)]
    (log/debugf "Implicit words: %s" (pr-str (map utils/sign->str implicit)))
    (concat
     (flatten (map sign-fn words))
     implicit)))

(defn prune
  "Remove dupe pair"
  [mask pair]
  (not (contains? mask pair)))

(defn build-pairs
  "Build viable combinations between signs. Filter out by mask"
  [signs mask]
  (let [pairs (set (mapcat identity (map (fn [i1] (map (fn [i2] (vec [i1 i2])) signs)) signs)))]
    (filter (partial prune mask) pairs)))

(defn reset-indices [] (UnifyControl/startUnifySequence))

(defn combinate
  "Main function for combination"
  ([^Grammar grammar initial-signs max-depth]
   (let [signs (->> (concat initial-signs (additional-signs grammar))
                    (remove nil?)
                    (distinct))
         _ (log/debugf "%d initial signs" (count signs))
         ident (set (map #(vec [% %]) signs))]
     (combinate grammar nil signs ident 0 max-depth)))

  ([^Grammar grammar signs new-signs mask depth max-depth]
   (if (and (seq new-signs) (not (> depth max-depth)))
     (let [coll (set (concat signs new-signs))
           pairs (build-pairs coll mask)
           results (doall (flatten (items->combinations (.rules grammar) pairs)))]
       (log/debugf "Depth: %d. There is %d new signs." depth (count new-signs))
       (combinate grammar coll (difference (set results) coll) (set (concat mask pairs)) (inc depth) max-depth))
     signs)))
