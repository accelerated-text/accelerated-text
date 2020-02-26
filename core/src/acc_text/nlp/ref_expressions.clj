(ns acc-text.nlp.ref-expressions
  (:require [acc-text.nlg.utils.nlp :as nlp]
            [clojure.tools.logging :as log]
            [clojure.string :as string]))

(defn filter-by-refs-count
  [[_ refs]]
  (>= (count refs) 2))

(defn filter-last-location-token
  [all-tokens group]
  (filter (fn [[idx token]]
            (let [next-token (nth all-tokens (inc idx) "$")]
              (log/tracef "Idx: %s Token: %s Next Token: %s" idx token next-token)
              ;; If it's last word in sentence, don't create ref.
              (not (= "." next-token))))
          group))

(defn referent? [token] (nlp/starts-with-capital? token))

(defn identify-potential-refs
  [tokens]
  (->> (map-indexed vector tokens)
       (filter #(referent? (second %)))
       (group-by second)
       (filter filter-by-refs-count)
       (map (comp rest second))
       (mapcat (partial filter-last-location-token tokens))))

(defmulti add-replace-token (fn [lang _] lang))

(defmethod add-replace-token :en [_ [idx value]]
  (if (nlp/ends-with-s? value) [idx "its"] [idx "it"]))

(defmethod add-replace-token :ee [_ [idx _]] [idx "see"])

(defmethod add-replace-token :de [_ [idx _]] [idx "es"])

(defmethod add-replace-token :default [_ _] nil)

(defn apply-ref-expressions
  [lang text]
  (let [tokens (nlp/tokenize text)
        refs (log/spy (identify-potential-refs tokens))
        smap (->> refs
                  (map (partial add-replace-token lang))
                  (into {}))]
    (log/debugf "Smap: %s" smap)
    (nlp/rebuild-sentences
     (map-indexed (fn
                    [idx v]
                    (if (contains? smap idx)
                      (get smap idx)
                      v))
                  tokens))))
