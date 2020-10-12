(ns data.spec.reader-model
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::code string?)

(s/def ::type #{:language :reader})

(s/def ::flag string?)

(s/def ::available? boolean?)

(s/def ::enabled? boolean?)
