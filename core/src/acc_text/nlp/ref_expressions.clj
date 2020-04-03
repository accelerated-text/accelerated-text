(ns acc-text.nlp.ref-expressions
  (:require [acc-text.nlp.utils :as nlp]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))

(defmulti ignored-token? (fn [lang _] lang))

(defmethod ignored-token? "Eng" [_ value] (contains? #{"i" "it"} (str/lower-case value)))

(defmethod ignored-token? :default [_ _] false)

(defn remove-ignored-tokens [lang [_ value]] (ignored-token? lang value))

(defn filter-by-refs-count [[_ refs]] (>= (count refs) 2))

(defn token-distance
  "Measure distance between tokens. First token may be already merged, so have multiple indices"
  [[d1 _] [d2 _]]
  (if (seq? d1)
    (- d2 (last d1))
    (- d2 d1)))

(defn merge-tokens [[p1 v1] [p2 v2]] [(flatten (seq [p1 p2])) (str/join " " (log/spyf :trace "Merging: %s" [v1 v2]))])

(defn merge-nearby
  "If there are two potential tokens nearby, merge them into single one"
  [tokens]
  (loop [[head next & tail] tokens
         result ()]
    (if (empty? next)
      (reverse (cons head result))
      (if (= 1 (token-distance head next))
        (recur (cons (merge-tokens head next) tail) result)
        (recur (cons next tail) (cons head result))))))

(defn next-index [idx]
  (if (seq? idx)
    (inc (last idx))
    (inc idx)))

(defn filter-last-location-token
  [all-tokens group]
  (filter (fn [[idx token]]
            (let [next-token (nth all-tokens (next-index idx) "$")]
              (log/tracef "Idx: %s Token: %s Next Token: %s" idx token next-token)
              ;; If it's last word in sentence, don't create ref.
              (not (= "." next-token))))
          group))

(defn referent? [token] (nlp/starts-with-capital? token))

(defn identify-potential-refs
  [lang tokens]
  (->> (map-indexed vector tokens)
       (filter #(referent? (second %)))
       (remove #(remove-ignored-tokens lang %))
       (merge-nearby)
       (group-by second)
       (filter filter-by-refs-count)
       (map (comp rest second))
       (mapcat (partial filter-last-location-token tokens))))

(defmulti add-replace-token (fn [lang _] lang))

(defmethod add-replace-token "Eng" [_ [idx value]]
  (if (nlp/ends-with-s? value) [idx "its"] [idx "it"]))

(defmethod add-replace-token "Est" [_ [idx _]] [idx "see"])

(defmethod add-replace-token "Ger" [_ [idx _]] [idx "es"])

(defmethod add-replace-token :default [_ [idx value]] [idx value])

(defn flatten-tokens
  "If tokens were merged, position consists of multiple indices. We need flat index structure in the end."
  [[idx value]]
  (if (seq? idx)
    (cons [(first idx) value] (map (fn [i] [i :delete]) (rest idx)))
    [[idx value]]))

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

(defn fix-corner-cases
  "If some cases needs too much effort to fix correctly, but are trivial to identify, we can just regex replace"
  [text]
  (str/replace text #"[Tt]he it" "it"))

(defn apply-ref-expressions
  [lang text]
  (let [tokens (nlp/tokenize text)
        smap (->> tokens
                  (identify-potential-refs lang)
                  (map (partial add-replace-token lang))
                  (check-for-dupes)
                  (mapcat flatten-tokens)
                  (into {}))]
    (log/tracef "Smap: %s" smap)
    (->> (map-indexed (fn [idx v]
                        (cond (= :delete (get smap idx)) ""
                              (contains? smap idx) (get smap idx)
                              :else v))
                      tokens)
         (nlp/rebuild-sentences)
         (fix-corner-cases))))
