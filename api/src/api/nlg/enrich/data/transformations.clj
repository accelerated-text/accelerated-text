(ns api.nlg.enrich.data.transformations
  (:require [clojure.string :as str]
            [numberwords.core :as nw]))

(defn number->words [s {:keys [language scale]
                        :or   {language :en, scale 1/4}}]
  (when-not (str/blank? s)
    (let [approx (nw/approximations language (Float/valueOf ^String s) scale)]
      (:numwords/text (some #(get approx %) [:numwords/equal :numwords/around :numwords/more-than :numwords/less-than])))))

(defn rough-estimation [s {:keys [places] :or {places 0}}]
  (if-not (str/blank? s)
    (let [n (Float/valueOf ^String s)
          abs-n (Math/abs ^Float n)]
      (letfn [(round [n]
                (let [multiplier (float (reduce * (take places (repeat 10))))]
                  (/ (Math/round ^Float (* n multiplier)) multiplier)))]
        (-> (cond
              (> 1000 abs-n) (str (round n))
              (> 1000000 abs-n) (str (round (/ n 1000)) "K")
              (> 1000000000 abs-n) (str (round (/ n 1000000)) "M")
              (> 1000000000000 abs-n) (str (round (/ n 1000000000)) "B")
              :else (str (round (/ abs-n 1000000000000)) "T"))
            (str/replace #"(\.[1-9]*0+)[KMBT]$" ""))))
    ""))

(defn add-symbol [s {:keys [symbol position skip] :or {position :back}}]
  (if (some? symbol)
    (let [n-chars-to-skip (if (seq skip)
                            (-> (format "%s[%s]+%s"
                                        (if (= :front position) "^" "")
                                        (str/join skip)
                                        (if (= :back position) "$" ""))
                                (re-pattern)
                                (re-find s)
                                (count))
                            0)]
      (if (= :front position)
        (str (subs s 0 n-chars-to-skip) symbol (subs s n-chars-to-skip))
        (str (subs s 0 (- (count s) n-chars-to-skip)) symbol (subs s (- (count s) n-chars-to-skip)))))
    s))

(defn custom-rearrange-1 [s {}]
  (if-not (str/blank? s)
    (let [[id main-cat & rest] (str/split s #"-")]
      (cond
        (seq rest) (format "%s (%s, %s)" (str/trim main-cat) (str/trim (str/join "-" rest)) (str/trim id))
        (some? main-cat) (format "%s (%s)" (str/trim main-cat) (str/trim id))
        :else id))
    ""))
