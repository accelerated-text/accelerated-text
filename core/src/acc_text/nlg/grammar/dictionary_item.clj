(ns acc-text.nlg.grammar.dictionary-item
  (:require [acc-text.nlg.grammar.utils :refer [escape-string]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defmulti build-dictionary-item (fn [type {:keys [category language]}]
                                  (str/join "/" [language type category])))

(defmethod build-dictionary-item :default [type {:keys [category language]}]
  (log/errorf "Unknown dictionary-item type pair for %s: %s/%s" language type category)
  "\"\"")

(defn join-forms [forms]
  (->> forms
       (map #(format "\"%s\"" (escape-string %)))
       (str/join " ")))

(defmethod build-dictionary-item "Eng/V/V" [_ {:keys [forms]}]
  (format "(ParadigmsEng.mkV %s)" (join-forms forms)))

(defmethod build-dictionary-item "Eng/A/A" [_ {:keys [forms]}]
  (format "(ParadigmsEng.mkA %s)" (join-forms forms)))

(defmethod build-dictionary-item "Eng/V2/V" [_ {:keys [forms]}]
  (format "(ParadigmsEng.mkV2 (ParadigmsEng.mkV %s))" (join-forms forms)))

(defmethod build-dictionary-item "Eng/N/N" [_ {:keys [forms attributes]}]
  (if (contains? attributes "Gender")
    (format "(ParadigmsEng.mkN ParadigmsEng.%s (ParadigmsEng.mkN %s))" (get attributes "Gender") (join-forms forms))
    (format "(ParadigmsEng.mkN %s)" (join-forms forms))))

(defmethod build-dictionary-item "Rus/A/A" [_ {:keys [forms]}]
  (format "(ParadigmsRus.mkA %s)" (join-forms forms)))

(defmethod build-dictionary-item "Rus/N/N" [_ {:keys [forms attributes]}]
  (format "(ParadigmsRus.mkN %s MorphoRus.%s MorphoRus.%s)"
          (join-forms forms) (get attributes "Gender") (get attributes "Animacy")))

(defmethod build-dictionary-item "Rus/V2/V" [_ {:keys [forms attributes]}]
  (format "(ParadigmsRus.mkV2 (ParadigmsRus.mkV MorphoRus.%s %s) \"\" ParadigmsRus.%s)"
          (get attributes "Aspect") (join-forms forms) (get attributes "Case")))

(defmethod build-dictionary-item "Rus/V/V" [_ {::dictionary-item/keys [forms attributes]}]
  (format "(ParadigmsRus.mkV MorphoRus.%s %s)" (get attributes "Aspect") (join-forms forms)))
