(ns data.spec.user-group
    (:require [clojure.spec.alpha :as s]
              [data.spec.data-file :as data-file]))

(s/def ::id number?)

(s/def ::data-files (s/coll-of ::data-file/id))

(s/def ::document-plans (s/coll-of string?))