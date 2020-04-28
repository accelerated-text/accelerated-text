(ns acc-text.nlg.gf.parser
  (:require [clojure.string :as str]
            [clojure.zip :as zip]))

(defn get-attrs [cat]
  (when (some? cat)
    (let [[_ type position] (re-find #"(.+?)(\d+)" cat)]
      {:type     (keyword (str/replace type #"_" "-"))
       :position (Integer/parseInt position)})))

(defn make-concept [{:keys [cat text]} lincat]
  (if (some? cat)
    (cond-> (get-attrs cat)
            (contains? lincat cat) (assoc :category (get lincat cat)))
    {:type     :quote
     :value    text
     :category "Str"}))

(defn make-zipper [t]
  (zip/zipper
    map?
    (fn [node]
      (:children node))
    (fn [node children]
      (assoc node :children children))
    t))

(defn detokenize [tokens]
  (str/replace (str/join " " tokens) #"\s+[.?!]" #(str/trim %)))

(defn parse-tree [t lincat]
  (loop [z (make-zipper (first t))
         new-paragraph? false
         new-sentence? false
         tokens []]
    (if (zip/end? z)
      {:text   (detokenize tokens)
       :tokens tokens}
      (let [node (cond->> (zip/node z) (string? (zip/node z)) (hash-map :text))
            {:keys [type category value]} (make-concept node lincat)]
        (recur
          (zip/next z)
          (cond-> new-paragraph?
                  (and (false? new-paragraph?) (= :segment type)) (not)
                  (and (true? new-paragraph?) (= :quote type)) (not))
          (cond-> new-sentence?
                  (and (false? new-sentence?) (= "Text" category)) (not)
                  (and (true? new-sentence?) (= :quote type)) (not))
          (cond-> tokens
                  (and (= :quote type) (or (true? new-sentence?) (zero? (count tokens)))) (conj (str/capitalize value))
                  (and (false? new-sentence?) (pos-int? (count tokens)) (= :quote type)) (conj value)))))))
