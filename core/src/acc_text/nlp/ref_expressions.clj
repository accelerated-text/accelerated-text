(ns acc-text.nlp.ref-expressions
  (:require [acc-text.nlg.utils.nlp :as nlp]
            [clojure.tools.logging :as log]))

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

(defn identify-potential-refs
  [tokens]
  (->> (map-indexed vector tokens)
       (filter #(nlp/starts-with-capital? (second %)))
       (group-by second)
       (filter filter-by-refs-count)
       (map (comp rest second))
       (mapcat (partial filter-last-location-token tokens))))

(defn add-replace-token-en
  [[idx value]]
  (cond
    (nlp/ends-with-s? value) [idx "its"]
    :else                    [idx "it"]))

(defn add-replace-token-ee
  [[idx _]]
  [idx "see"])

(defn add-replace-token-de
  [[idx _]]
  [idx "es"])

(defn add-replace-token
  [lang args]
  (case lang
    :en (add-replace-token-en args)
    :de (add-replace-token-de args)
    :ee (add-replace-token-ee args)
    nil))

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
