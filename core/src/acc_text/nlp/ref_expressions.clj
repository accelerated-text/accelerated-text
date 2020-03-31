(ns acc-text.nlp.ref-expressions
  (:require [acc-text.nlp.utils :as nlp]
            [clojure.tools.logging :as log]))

(defmulti ignored-token? (fn [lang _] lang))

(defmethod ignored-token? "Eng" [_ value] (contains? #{"I"} value))

(defmethod ignored-token? :default [_ _] false)

(defn remove-ignored-tokens [lang [value _]] (ignored-token? lang value))

(defn filter-by-refs-count [[_ refs]] (>= (count refs) 2))

(defn merge-nearby [tokens]
  (loop [pairs (partition 2 1 tokens)
         final ()]
    (if (empty? pairs)
      (reverse final)
      (let [[head & tail] pairs
            [[p1 v1] [p2 v2]] head]
        (if (= 1 (- p2 p1))  ;; If distance between words is one token - merge them
          (recur (rest tail) (concat [[p2 ""] [p1 (clojure.string/join " " (log/spyf :debug "Merging: %s" [v1 v2]))]] final))
          (recur tail (cons [p1 v1] final))))))) ;; Otherwise, put first one into the list and continue forward


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
  [lang tokens]
  (->> (map-indexed vector tokens)
       (filter #(referent? (second %)))
       (merge-nearby)
       (group-by second)
       (remove #(remove-ignored-tokens lang %))
       (filter filter-by-refs-count)
       (map (comp rest second))
       (mapcat (partial filter-last-location-token tokens))))

(defmulti add-replace-token (fn [lang _] lang))

(defmethod add-replace-token "Eng" [_ [idx value]]
  (if (nlp/ends-with-s? value) [idx "its"] [idx "it"]))

(defmethod add-replace-token "Est" [_ [idx _]] [idx "see"])

(defmethod add-replace-token "Ger" [_ [idx _]] [idx "es"])

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
                  (identify-potential-refs lang)
                  (map (partial add-replace-token lang))
                  (check-for-dupes)
                  (into {}))]
    (log/debugf "Smap: %s" smap)
    (nlp/rebuild-sentences
      (map-indexed (fn [idx v]
                     (cond (= :delete (get smap idx)) ""
                           (contains? smap idx) (get smap idx)
                           :else v))
                   tokens))))
