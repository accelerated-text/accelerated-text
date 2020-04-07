(ns acc-text.nlg.grammar.dictionary-item
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))

(defmulti build-dictionary-item (fn [type {:keys [category language]}]
                                  (str/join "/" [language (or type "s:Str") category])))

(defmethod build-dictionary-item :default [type {:keys [category language forms]}]
  (log/warnf "Unknown dictionary-item type pair for %s: %s/%s" language (or type "s:Str") category)
  (format "\"%s\"" (if (some? (first forms)) (escape-string (first forms)) "")))

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

(defmethod build-dictionary-item "Eng/Str/Str" [_ {:keys [forms]}]
  (format "\"%s\"" (escape-string (first forms))))

(defmethod build-dictionary-item "Eng/s:Str/Str" [_ {:keys [forms]}]
  (format "{s = \"%s\"}" (escape-string (first forms))))

(defmethod build-dictionary-item "Eng/s:Str/N" [_ {:keys [forms attributes]}]
  (format "(mkText (mkCl %s))"
          (if (contains? attributes "Gender")
            (format "(ParadigmsEng.mkN ParadigmsEng.%s (ParadigmsEng.mkN %s))" (get attributes "Gender") (join-forms forms))
            (format "(ParadigmsEng.mkN %s)" (join-forms forms)))))

(defmethod build-dictionary-item "Eng/s:Str/V" [_ {:keys [forms]}]
  (format "(mkText (mkCl %s))" (format "(ParadigmsEng.mkV %s)" (join-forms forms))))

(defmethod build-dictionary-item "Ger/V/V" [_ {:keys [forms]}]
  (format "(ParadigmsGer.mkV %s)" (join-forms forms)))

(defmethod build-dictionary-item "Rus/A/A" [_ {:keys [forms]}]
  (format "(ParadigmsRus.mkA %s)" (join-forms forms)))

(defmethod build-dictionary-item "Rus/N/N" [_ {:keys [forms attributes]}]
  (format "(ParadigmsRus.mkN %s MorphoRus.%s MorphoRus.%s)"
          (join-forms (if (= 1 (count forms)) (repeat 13 (first forms)) forms))
          (get attributes "Gender" "Masc")
          (get attributes "Animacy" "Inanimate")))

(defmethod build-dictionary-item "Rus/V2/V" [_ {:keys [forms attributes]}]
  (format "(ParadigmsRus.mkV2 (ParadigmsRus.mkV MorphoRus.%s %s) \"\" ParadigmsRus.%s)"
          (get attributes "Aspect") (join-forms forms) (get attributes "Case")))

(defmethod build-dictionary-item "Rus/V/V" [_ {:keys [forms attributes]}]
  (format "(ParadigmsRus.mkV MorphoRus.%s %s)" (get attributes "Aspect") (join-forms forms)))

(defmethod build-dictionary-item "Rus/Str/Str" [_ {:keys [forms]}]
  (format "\"%s\"" (escape-string (first forms))))

(defmethod build-dictionary-item "Rus/s:Str/Str" [_ {:keys [forms]}]
  (format "{s = \"%s\"}" (escape-string (first forms))))

(defmethod build-dictionary-item "Rus/s:Str/N" [_ {:keys [forms attributes]}]
  (format "(mkText (mkCl (ParadigmsRus.mkN %s MorphoRus.%s MorphoRus.%s)))"
          (join-forms forms) (get attributes "Gender" "Masc") (get attributes "Animacy" "Inanimate")))

(defmethod build-dictionary-item "Rus/s:Str/V" [_ {:keys [forms attributes]}]
  (format "(mkText (mkCl (ParadigmsRus.mkV MorphoRus.%s %s)))" (get attributes "Aspect") (join-forms forms)))
