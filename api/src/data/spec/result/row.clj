(ns data.spec.result.row
  (:require [clojure.spec.alpha :as s]
            [data.spec.result.annotation :as annotation]))

(s/def ::id string?)

(s/def ::text string?)

(s/def ::language #{"Eng" "Est" "Ger" "Lav" "Rus"})

(s/def ::readers (s/coll-of string?))

(s/def ::enriched? boolean?)

(s/def ::annotation (s/keys ::req [::annotation/id ::annotation/idx ::annotation/text]))

(s/def ::annotations (s/coll-of ::annotation))
