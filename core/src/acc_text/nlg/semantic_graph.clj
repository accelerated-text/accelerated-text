(ns acc-text.nlg.semantic-graph
  (:require [acc-text.nlg.semantic-graph.concept :as concept]
            [acc-text.nlg.semantic-graph.relation :as relation]
            [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::uid string?)

(s/def ::concepts
  (s/coll-of
    (s/keys :req-un [::concept/id ::concept/type]
            :opt-un [::concept/name ::concept/value ::concept/category ::concept/label ::concept/position])))

(s/def ::relations
  (s/coll-of
    (s/keys :req-un [::relation/from ::relation/to ::relation/role]
            :opt-un [::relation/index ::relation/category ::relation/name])))

(s/def ::name string?)

(s/def ::kind string?)

(s/def ::category string?)

(s/def ::description string?)

(s/def ::graph
  (s/keys :req [::id ::concepts ::relations]
          :opt [::uid ::name ::kind ::category ::description]))
