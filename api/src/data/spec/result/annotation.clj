(ns data.spec.result.annotation
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::idx int?)

(s/def ::text string?)
