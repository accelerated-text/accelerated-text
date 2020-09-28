(ns api.nlg.enrich.data.transformations
  (:require [clojure.string :as str]
            [numberwords.core :as nw]))

(defn approximate [s {:keys [language scale]
                      :or   {language :en, scale 1/4}}]
  (when-not (str/blank? s)
    (let [approx (nw/approximations language (Float/valueOf ^String s) scale)]
      (:numwords/text (some #(get approx %) [:numwords/equal :numwords/around :numwords/more-than :numwords/less-than])))))

(defn rough-estimation [s {}]
  (let [n (Float/valueOf ^String s)
        abs-n (Math/abs ^Float n)]
    (str (when (> 0 n) "-")
         (cond
           (> 1000 abs-n) (str (Math/round ^Float n))
           (> 1000000 abs-n) (str (Math/round ^Float (/ n 1000)) "K")
           (> 1000000000 abs-n) (str (Math/round ^Float (/ n 1000000)) "M")
           (> 1000000000000 abs-n) (str (Math/round ^Float (/ n 1000000000)) "B")
           :else (str (Math/round ^Float (/ abs-n 1000000000000)) "T")))))
