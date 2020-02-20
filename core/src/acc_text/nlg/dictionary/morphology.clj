(ns acc-text.nlg.dictionary.morphology
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def word? (s/and string? (complement str/blank?)))

(s/def ::key word?)

(s/def ::pos #{:n :v :a})

(s/def ::gender #{:f :m :n})

(s/def ::language keyword?)

(s/def ::case #{:nom :gen :dat :acc :inc :loc :voc})

(s/def ::number #{:sg :pl})

(s/def ::inflections (s/map-of (s/tuple ::case ::number)
                          word?))

(s/def ::word-def (s/keys :req [::key ::pos ::language]
                               :opt [::gender ::inflections]))
