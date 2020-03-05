(ns acc-text.nlg.gf.generator.dictionary-item
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.gf.generator.utils :as gen-utils]
            [clojure.string :as str]))

(defmulti build-dictionary-item (fn [type {::dictionary-item/keys [category language]}]
                                  (str/join "/" [language type category])))

(defn join-forms [forms]
  (->> forms
       (map #(format "\"%s\"" (gen-utils/escape-string %)))
       (str/join " ")))

(defmethod build-dictionary-item "Eng/V/V" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsEng.mkV %s)" (join-forms forms)))

(defmethod build-dictionary-item "Eng/V2/V" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsEng.mkV2 (ParadigmsEng.mkV %s))" (join-forms forms)))

(defmethod build-dictionary-item "Eng/V2/V2" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsEng.mkV2 (ParadigmsEng.mkV %s))" (join-forms forms)))

(defmethod build-dictionary-item "Eng/V/V2" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsEng.mkV %s)" (join-forms forms)))

(defmethod build-dictionary-item "Eng/N/N" [_ {::dictionary-item/keys [forms attributes]}]
  (if (contains? attributes "Gender")
    (format "(ParadigmsEng.mkN ParadigmsEng.%s (ParadigmsEng.mkN %s))" (get attributes "Gender") (join-forms forms))
    (format "(ParadigmsEng.mkN %s)" (join-forms forms))))
