(ns acc-text.nlg.dictionary.item
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def word? (s/and string? (complement str/blank?)))

(s/def ::id string?)

(s/def ::key word?)

(s/def ::sense string?)

(s/def ::definition string?)

(s/def ::category
  #{"A" "A2" "Adv"
    "N" "N2" "N3"
    "PN"
    "V" "V0" "V2" "V3" "V2A" "V2Q" "V2S" "V2V" "VA" "VP" "VQ" "VS" "VV"})

(s/def ::language #{"Eng" "Ger" "Est" "Lit" "Lav" "Rus"})

(s/def ::forms (s/coll-of word?))

(s/def ::attributes (s/map-of string? string?))
