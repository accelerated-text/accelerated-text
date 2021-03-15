(ns acc-text.nlg.dictionary.impl
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.dictionary.lang.eng :as eng]))

(defmulti resolve-dict-item ::dict-item/language)

(defmethod resolve-dict-item "Eng" [dict-item]
  (eng/resolve-dict-item dict-item))
