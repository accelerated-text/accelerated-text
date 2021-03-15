(ns acc-text.nlg.dictionary.lang.other
  (:require [acc-text.nlg.dictionary.item :as dict-item]
            [clojure.string :as str]))

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))

(defn join-forms [forms]
  (->> forms
       (map #(format "\"%s\"" (escape-string (::dict-item-form/value %))))
       (str/join " ")))

(defmulti resolve-dict-item (fn [{::dict-item/keys [language category]}]
                              (str/join "/" [language category])))

(defmethod resolve-dict-item :default [{::dict-item/keys [language category]}]
  (throw (Exception. (format "Don't know how to resolve dictionary item category `%s` for language `%s`" category language))))

(defmethod resolve-dict-item "Spa/N" [{::dict-item/keys [forms attributes]}]
  (if (contains? attributes "Gender")
    (format "(ParadigmsSpa.mkN %s ParadigmsSpa.%s)" (join-forms forms) (get attributes "Gender"))
    (format "(ParadigmsSpa.mkN %s)" (join-forms forms))))

(defmethod resolve-dict-item "Spa/V" [{::dict-item/keys [forms]}]
  (format "(ParadigmsSpa.mkV %s)" (join-forms forms)))

(defmethod resolve-dict-item "Spa/A" [{::dict-item/keys [forms]}]
  (format "(ParadigmsSpa.mkA %s)" (join-forms forms)))

(defmethod resolve-dict-item "Spa/Adv" [{::dict-item/keys [forms]}]
  (format "(ParadigmsSpa.mkAdv %s)" (join-forms forms)))

(defmethod resolve-dict-item "Spa/AdV" [{::dict-item/keys [forms]}]
  (format "(ParadigmsSpa.mkAdV %s)" (join-forms forms)))

(defmethod resolve-dict-item "Ger/V" [{::dict-item/keys [forms]}]
  (format "(ParadigmsGer.mkV %s)" (join-forms forms)))

(defmethod resolve-dict-item "Rus/A" [{::dict-item/keys [forms]}]
  (format "(ParadigmsRus.mkA %s)" (join-forms forms)))

(defmethod resolve-dict-item "Rus/N" [{::dict-item/keys [forms attributes]}]
  (format "(ParadigmsRus.mkN %s MorphoRus.%s MorphoRus.%s)"
          (join-forms (if (= 1 (count forms)) (repeat 13 (first forms)) forms))
          (get attributes "Gender" "Masc")
          (get attributes "Animacy" "Inanimate")))

(defmethod resolve-dict-item "Rus/V" [{::dict-item/keys [forms attributes]}]
  (format "(ParadigmsRus.mkV MorphoRus.%s %s)" (get attributes "Aspect") (join-forms forms)))
