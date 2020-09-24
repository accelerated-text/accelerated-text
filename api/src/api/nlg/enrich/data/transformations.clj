(ns api.nlg.enrich.data.transformations
  (:require [clojure.string :as str]
            [numberwords.core :as nw]))

(defn approximate [s {:keys [language scale]
                      :or   {language :en, scale 1/4}}]
  (when-not (str/blank? s)
    (let [approx (nw/approximations language (Float/valueOf s) scale)]
      (:numwords/text (some #(get approx %) [:numwords/equal :numwords/around :numwords/more-than :numwords/less-than])))))
