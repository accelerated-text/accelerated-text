(ns acc-text.nlg.spec.feature-set
  (:require [clojure.spec.alpha :as s]
            [acc-text.nlg.spec.common :as common]))

(s/def ::attribute common/simple-string?)

(s/def ::value common/simple-string?)

(s/def ::feature-type #{:featvar :nomvar :feat})

(s/def ::feature
  (s/keys :req [::attribute ::feature-type ::value]))

(s/def ::index (s/and int? pos?))

(s/def ::inherits-from (s/and int? pos?))

(s/def ::feature-set
  (s/cat :id ::index
         :inherits-from ::inherits-from
         :attr ::attribute
         :val ::value
         :features (s/coll-of ::feature :gen-max 3)))
