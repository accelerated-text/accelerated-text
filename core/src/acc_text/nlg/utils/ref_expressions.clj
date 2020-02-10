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

(defn add-replace-token-en
  [[idx value]]
  (cond
    (nlp/ends-with-s? value) [idx "its"]
    :else [idx "it"]))

(defn add-replace-token
  [lang args]
  (case lang
    :en (add-replace-token-en args)
    (add-replace-token-en args)))

(defn apply-ref-expressions
  [lang text]
  (let [tokens (nlp/tokenize text)
        refs (identify-potential-refs tokens)
        smap (->> refs
                  (map rest)
                  (mapcat identity)
                  (map (partial add-replace-token lang))
                  (into {}))]
    (nlp/rebuild-sentences
     (map-indexed (fn
                    [idx v]
                    (if (contains? smap idx)
                      (get smap idx)
                      v))
                  tokens))))
