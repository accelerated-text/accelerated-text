(ns data.spec.user-group
    (:require [clojure.spec.alpha :as s]
              [data.spec.data-file :as data-file]))

(s/def ::id long?)

(s/def ::data-files (s/coll-of ::data-file/id))