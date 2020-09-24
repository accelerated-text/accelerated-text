(ns api.nlg.enrich.data.transformations
  (:require [numberwords.core :as nw]))

(defn approximate [s {:keys [language scale]
                      :or   {language :en, scale 1/4}}]
  (let [approx (nw/approximations language (Float/valueOf s) scale)]
    (:numwords/text (some #(get approx %) [:numwords/equal :numwords/around :numwords/more-than :numwords/less-than]))))
