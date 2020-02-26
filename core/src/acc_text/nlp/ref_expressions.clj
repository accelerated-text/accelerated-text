(ns acc-text.nlp.ref-expressions
  (:require [acc-text.nlg.utils.nlp :as nlp]
            [clojure.tools.logging :as log]))

(defn filter-by-refs-count [[_ refs]] (>= (count refs) 2))

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

(defn check-for-dupes
  "If we have replacement tokens which are exact same in a row [it, it] then mark the next one
  for deletion"
  [replace-tokens]
  (if (= 1 (count replace-tokens))
    replace-tokens
    (loop [[[_ token1 :as t1] [idx2 token2 :as t2] & tokens] replace-tokens
           deduped []]
      (if (empty? t2)
        (concat deduped [t1])
        (if (= token1 token2)
          (recur tokens (concat deduped [t1 [idx2 :delete]]))
          (recur tokens deduped))))))

(defn apply-ref-expressions
  [lang text]
  (let [tokens (nlp/tokenize text)
        smap (->> tokens
                  (identify-potential-refs)
                  (map (partial add-replace-token lang))
                  (check-for-dupes)
                  (into {}))]
    (nlp/rebuild-sentences
     (map-indexed (fn [idx v]
                    (cond (= :delete (get smap idx)) ""
                          (contains? smap idx) (get smap idx)
                          :else v))
                  tokens))))
