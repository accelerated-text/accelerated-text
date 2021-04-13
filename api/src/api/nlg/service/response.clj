(ns api.nlg.service.response
  (:require [clojure.spec.alpha :as s]))

(s/def ::resultId string?)

(s/def ::resultIds (s/coll-of ::resultId))

(s/def ::offset int?)

(s/def ::totalCount int?)

(s/def ::ready boolean?)

(s/def ::updatedAt number?)

(s/def ::variants (s/coll-of string?))

(s/def ::error boolean?)

(s/def ::message string?)
