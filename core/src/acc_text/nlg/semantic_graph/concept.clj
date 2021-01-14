(ns acc-text.nlg.semantic-graph.concept
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::type #{:document-plan :segment :amr :data :quote :dictionary-item :modifier
                :sequence :shuffle :synonyms :condition :if-statement :else-statement
                :comparator :boolean :variable :constatnt :reference :operation})

(s/def ::name string?)

(s/def ::value string?)

(s/def ::category string?)

(s/def ::label string?)

(s/def ::position number?)
