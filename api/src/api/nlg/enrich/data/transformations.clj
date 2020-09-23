(ns api.nlg.enrich.data.transformations
  (:require [numberwords.core :as nw]))

(defn approximate [s {:keys [language scale]
                      :or   {language :en, scale 1/4}}]
  (:numwords/text (:numwords/around (nw/approximations language (Float/valueOf ^String s) scale))))
