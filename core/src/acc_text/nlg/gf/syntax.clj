(ns acc-text.nlg.gf.syntax
  (:require [clojure.spec.alpha :as s]))

(s/def ::label keyword?)

(s/def ::symbol string?)

(s/def ::literal string?)

(s/def ::value (s/or ::symbol ::literal))

(s/def ::values (s/coll-of ::value :min-count 1))

(s/def ::row (s/keys :req [::label ::symbol ::values]))
