(ns api.nlg.service.request
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::documentPlanId string?)

(s/def ::documentPlanName string?)

(s/def ::dataId string?)

(s/def ::format #{"raw" "annotated-text" "annotated-text-shallow"})

(s/def ::dataRow (s/map-of string? string?))

(s/def ::dataRows (s/map-of ::id ::dataRow))

(s/def ::readerFlagValues (s/map-of string? boolean?))

(s/def ::sampleMethod #{"first" "relevant"})

(s/def ::async boolean?)
