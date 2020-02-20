(ns acc-text.nlg.dictionary.morphology
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def word? (s/and string? (complement str/blank?)))

(s/def :morph/base-form word?)

(s/def :morph/pos #{:n :v :a})

(s/def :morph/gender #{:f :m :n})

(s/def :morph/case #{:nom :gen :dat :acc :inc :loc :voc})

(s/def :morph/number #{:sg :pl})

(s/def :morph/inflections (s/map-of (s/tuple :morph/case :morph/number)
                          word?))

(s/def :morph/word-def (s/keys :req [:morph/base-form :morph/pos]
                               :opt [:morph/gender :morph/inflections]))

