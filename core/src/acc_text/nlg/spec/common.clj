(ns acc-text.nlg.spec.common
  (:require [clojure.spec.alpha :as s]))

(def simple-string? (s/and string? #(not= % "")))
