(ns acc-text.nlg.grammar.data-item
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]))

(defn join-forms [forms] (str/join " " forms))

(defn russian-forms [category form]
  (let [quoted-form (format "\"%s\"" form)]
    (condp = category
      "N" (format "(ParadigmsRus.mkN %s Masc Inanimate)"
                  (join-forms
                   ;; This is intended for words which are not found in the dictionary
                   ;; most likely those will be non Russian names like 'NoFrost'
                   ;; Need to repeat the same 13 times, for GF not to start auto-adding
                   ;; Russian endings for cases.
                   ;; FIXME
                   ;; Note hardcoded 'Masc Inanimate'
                   (repeat 13 quoted-form)))
      "A" (format "(ParadigmsRus.mkA %s)" quoted-form)
      nil (do
            (log/errorf "Unknown category for data type for %s: %s. Building with cat N" category form)
            ;; FIXME we get there via data use in IFs is this the best way to handle it?
            (format "(ParadigmsRus.mkN %s Masc Inanimate)" (join-forms (repeat 13 quoted-form)))
            nil))))

(defn english-forms [category form]
  ;;FIXME see comment in russian-forms
  (format "(ParadigmsEng.mk%s %s)" (or category "N") form))

(defn build-data-item [category form language]
  (let [quoted-form (format "\"%s\"" form)]
    (condp = language
      "Eng" (english-forms category quoted-form)
      "Rus" (russian-forms category form)
      (do
        (log/errorf "Unknown Data item type for %s: %s" language form)
        nil))))
