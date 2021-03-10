(ns acc-text.nlg.paradigms.lang
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [acc-text.nlg.paradigms.lang.eng :as eng]))

(def dict-categories #{"mkA" "mkA2"
                       "mkAdA" "mkAdN" "mkAdV" "mkAdv"
                       "mkConj"
                       "mkIP"
                       "mkInterj"
                       "mkN" "mkN2" "mkNP" "mkPN"
                       "mkPost" "mkPrep"
                       "mkPron"
                       "mkQuant"
                       "mkSubj"
                       "mkV" "mkV2" "mkV2A" "mkV2S" "mkV2V" "mkV3" "mkVA" "mkVQ" "mkVS" "mkVV"})

(defmulti resolve-dict-item ::dict-item/language)

(defmethod resolve-dict-item "Eng" [dict-item]
  (eng/resolve-dict-item dict-item))
