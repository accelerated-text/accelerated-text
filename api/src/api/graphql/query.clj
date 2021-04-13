(ns api.graphql.query
  (:require [clojure.spec.alpha :as s]))

(s/def ::query string?)

(s/def ::variables map?)

(s/def ::context map?)

(s/def ::body
  (s/keys :req-un [::query]
          :opt-un [::variables ::context]))
