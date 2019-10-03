(ns acc-text.nlg.spec.morphology
  (:require [acc-text.nlg.spec.common :as common]
            [acc-text.nlg.spec.feature-set :as fs]
            [clojure.spec.alpha :as s]))

(s/def ::word common/simple-string?)

(s/def ::stem common/simple-string?)

(s/def ::class common/simple-string?)

(s/def ::predicate common/simple-string?)

(s/def ::macros common/simple-string?)

(s/def ::name common/simple-string?)

(s/def ::pos
  #{:S :NNP :NP :CC :VB :PRP})

;;Lexeme pairs a word or phrase with a list of logical constants that can be used
;;to construct its meaning.
;;For example,one lexeme might be(Boston, bos, [location, destination]).
(s/def ::lexeme
  (s/keys :req [::word ::pos]
          :opt [::stem ::predicate ::class ::fs/feature-set ::macros]))

(s/def ::morphology
  (s/coll-of ::lexeme :min-count 1))

(s/def ::macro
  (s/cat ::name ::fs/feature-set))

