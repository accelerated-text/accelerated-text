(ns data.spec.result
  (:require [clojure.spec.alpha :as s]
            [data.spec.result.row :as row]))

(s/def ::id string?)

(s/def ::status #{:pending :ready :error})

(s/def ::error-message string?)

(s/def ::timestamp number?)

(s/def ::row (s/keys :req [::row/id ::row/text ::row/language]
                     :opt [::row/annotations ::row/enriched? ::row/readers]))

(s/def ::rows (s/coll-of ::row))
