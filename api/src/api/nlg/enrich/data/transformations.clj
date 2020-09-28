(ns api.nlg.enrich.data.transformations
  (:require [clojure.string :as str]
            [numberwords.core :as nw]))

(defn number->words [s {:keys [language scale]
                        :or   {language :en, scale 1/4}}]
  (when-not (str/blank? s)
    (let [approx (nw/approximations language (Float/valueOf ^String s) scale)]
      (:numwords/text (some #(get approx %) [:numwords/equal :numwords/around :numwords/more-than :numwords/less-than])))))

(defn rough-estimation [s {}]
  (let [n (Float/valueOf ^String s)
        abs-n (Math/abs ^Float n)]
    (cond
      (> 1000 abs-n) (str (Math/round ^Float n))
      (> 1000000 abs-n) (str (Math/round ^Float (/ n 1000)) "K")
      (> 1000000000 abs-n) (str (Math/round ^Float (/ n 1000000)) "M")
      (> 1000000000000 abs-n) (str (Math/round ^Float (/ n 1000000000)) "B")
      :else (str (Math/round ^Float (/ abs-n 1000000000000)) "T"))))

(defn add-symbol [s {:keys [symbol position] :or {position :back}}]
  (let [cond->? (if (= :back position) cond->> cond->)]
    (cond->? s (some? symbol) (str symbol))))

(defn custom-rearrange-1 [s {}]
  (let [[id main-cat & rest] (str/split s #"-")]
    (format "%s (%s, %s)" (str/trim main-cat) (str/trim (str/join "-" rest)) (str/trim id))))
