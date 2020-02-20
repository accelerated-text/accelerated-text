(ns acc-text.nlg.dictionary.morphology
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def word? (s/and string? (complement str/blank?)))


(s/def ::key word?)

(s/def ::sense (s/coll-of number? :min-count 1))

(s/def ::defintion string?)

(s/def ::pos #{:a2 :adn :adv :cadv :interj :n3 :number
               :v2a :v2q :v2v :va :vq :vv :ada :a
               :n2 :n :pn :type :v2 :v2s :v3 :v :vs})

(s/def ::gender #{:f :m :n})

(s/def ::language #{:bul :cat :chi :dut :eng :est
                    :fin :fra :ger :ita :lat :lit
                    :por :slv :spa :swe :tha :tur})

(s/def ::case #{:nom :gen :dat :acc :inc :loc :voc})

(s/def ::number #{:sg :pl})

(s/def ::inflections (s/map-of (s/tuple ::case ::number)
                          word?))

(s/def ::word-def (s/keys :req [::key ::pos ::language]
                          :opt [::sense ::definition ::gender ::inflections]))

