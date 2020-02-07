(ns acc-text.nlg.utils.ref-expressions
  (:require [acc-text.nlg.utils.nlp :as nlp]))

(defn filter-by-refs-count
  [[k refs]]
  (>= (count refs) 2))

(defn identify-potential-refs
  [tokens]
  (->> (map-indexed vector tokens)
       (filter #(nlp/starts-with-capital? (second %)))
       (group-by second)
       (vec)
       (filter filter-by-refs-count)
       (map second)))

(defn add-replace-token
  [idx]
  [idx "it"])

(defn apply-ref-expressions
  [text]
  (let [tokens (nlp/tokenize text)
        refs (identify-potential-refs tokens)
        smap (->> refs
                  (map rest)
                  (mapcat identity)
                  (map first)
                  (map add-replace-token)
                  (into {}))]
    (nlp/rebuild-sentences
     (map-indexed (fn
                    [idx v]
                    (if (contains? smap idx)
                      (get smap idx)
                      v))
                  tokens))))
