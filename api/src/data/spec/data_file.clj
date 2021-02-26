(ns data.spec.data-file
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)

(s/def ::name string?)

(s/def ::timestamp inst?)

(s/def ::content any?)
