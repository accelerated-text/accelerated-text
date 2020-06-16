(ns data.spec.language
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::code string?)

(s/def ::enabled? boolean?)
