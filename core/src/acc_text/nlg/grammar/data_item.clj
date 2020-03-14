(ns acc-text.nlg.grammar.data-item
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]))

(defn join-forms [forms] (str/join " " forms))

(defn build-data-item [form language]
  (let [quoted-form (format "\"%s\"" form)]
    (condp = language
      "Eng" (format "(ParadigmsEng.mkN %s)" quoted-form)
      "Rus" (format "(ParadigmsRus.mkN %s Masc Inanimate)"
                    (join-forms
                     ;; FIXME
                     ;; This is intended for words which are not found in the dictionary
                     ;; most likely those will be non Russian names like 'NoFrost'
                     ;; Need to repeat the same 13 times, for GF not to start auto-adding
                     ;; Russian endings for cases.
                     ;; Also note hardcoded 'Masc Inanimate'
                     (repeat 13 quoted-form)))
      (do
        (log/errorf "Unknown Data item type for %s: %s" language form)
        nil))))
