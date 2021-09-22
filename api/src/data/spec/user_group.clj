(ns data.spec.user-group
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [clojure.spec.alpha :as s]
            [data.spec.data-file :as data-file]
            [data.spec.reader-model :as reader-model]))

(s/def ::id number?)

(s/def ::data-files (s/coll-of ::data-file/id))

(s/def ::document-plans (s/coll-of :document-plan/id))

(s/def ::dictionary-items (s/coll-of ::dictionary-item/id))

(s/def ::reader-models (s/coll-of ::reader-model/code))
