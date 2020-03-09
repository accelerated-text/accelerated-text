(ns acc-text.nlg.generator.dictionary-item
  (:require [acc-text.nlg.dictionary.item :as dictionary-item]
            [acc-text.nlg.generator.utils :as gen-utils]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defmulti build-dictionary-item (fn [type {::dictionary-item/keys [category language]}]
                                  (str/join "/" [language type category])))

(defmethod build-dictionary-item :default [type {::dictionary-item/keys [category language]}]
  (log/errorf "Unknown dictionary-item type pair for %s: %s/%s" language type category)
  "\"\"")

(defn join-forms [forms]
  (->> forms
       (map #(format "\"%s\"" (gen-utils/escape-string %)))
       (str/join " ")))

(defmethod build-dictionary-item "Eng/V/V" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsEng.mkV %s)" (join-forms forms)))

(defmethod build-dictionary-item "Eng/A/A" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsEng.mkA %s)" (join-forms forms)))

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

(defmethod build-dictionary-item "Rus/A/A" [_ {::dictionary-item/keys [forms]}]
  (format "(ParadigmsRus.mkA %s)" (join-forms forms)))

(defmethod build-dictionary-item "Rus/N/N" [_ {::dictionary-item/keys [forms attributes]}]
  (format "(ParadigmsRus.mkN %s MorphoRus.%s MorphoRus.%s)"
          (join-forms forms) (get attributes "Gender") (get attributes "Animacy")))

(defmethod build-dictionary-item "Rus/V2/V" [_ {::dictionary-item/keys [forms attributes]}]
  (format "(ParadigmsRus.mkV2 (ParadigmsRus.mkV MorphoRus.%s %s) \"\" ParadigmsRus.%s)"
          (get attributes "Aspect") (join-forms forms) (get attributes "Case")))
