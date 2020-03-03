(ns acc-text.nlg.dictionary
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [clojure.spec.alpha :as s]))

(s/def ::item (s/keys :req [::dictionary-item/key
                            ::dictionary-item/category
                            ::dictionary-item/language
                            ::dictionary-item/forms]
                      :opt [::dictionary-item/sense
                            ::dictionary-item/definition
                            ::dictionary-item/attributes]))
