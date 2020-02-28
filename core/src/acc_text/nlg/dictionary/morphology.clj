(ns acc-text.nlg.dictionary.morphology
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def word? (s/and string? (complement str/blank?)))

(s/def ::key word?)

(s/def ::sense keyword?)

(s/def ::defintion string?)

(s/def ::pos #{"A" "A2" "Adv"
               "N" "N2" "N3"
               "PN"
               "V" "V0" "V2" "V3" "V2A" "V2Q" "V2S" "V2V" "VA" "VP" "VQ" "VS" "VV" })

(s/def ::gender #{:f :m :n})

(s/def ::language #{"Eng" "Ger" "Est" "Lit" "Lat" "Rus"})

(s/def ::inflections (s/map-of #{:nom-sg :nom-pl :gen-sg :gen-pl} word?))

(s/def ::tenses (s/map-of
                 #{:conditional-tense :future-tense :past-tense :present-tense}
                 word?))

(s/def ::word-def (s/keys :req [::key ::pos ::language]
                          :opt [::sense ::definition]))

(s/def ::verb-def (s/merge ::word-def (s/keys :req [::tenses])))

(s/def ::noun-def (s/merge ::word-def (s/keys :req [::inflections ::gender])))
