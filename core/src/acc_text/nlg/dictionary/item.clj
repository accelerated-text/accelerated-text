(ns acc-text.nlg.dictionary.item
  (:require [acc-text.nlg.dictionary.item.attr :as attr]
            [acc-text.nlg.dictionary.item.form :as form]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def word? (s/and string? (complement str/blank?)))

(s/def ::id string?)

(s/def ::key word?)

(s/def ::sense string?)

(s/def ::definition string?)

(s/def ::category
  #{"A" "A2"
    "AdA" "AdN" "AdV" "Adv"
    "Conj"
    "IP"
    "Interj"
    "N" "N2" "N3" "NP" "PN"
    "Post" "Prep"
    "Pron"
    "Quant"
    "Subj"
    "V" "V2" "V2A" "V2S" "V2V" "V2Q" "V3" "VA" "VQ" "VS" "VV"})

(s/def ::language #{"Eng" "Ger" "Est" "Lit" "Lav" "Rus"})

(s/def ::forms (s/keys :req [::form/id ::form/value]))

(s/def ::attributes (s/keys :req [::attr/id ::attr/name ::attr/value]))
