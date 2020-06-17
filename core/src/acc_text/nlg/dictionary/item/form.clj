(ns acc-text.nlg.dictionary.item.form
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::value string?)

(s/def ::default? boolean?)
