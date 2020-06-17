(ns acc-text.nlg.dictionary.item.attr
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::name string?)

(s/def ::value string?)
