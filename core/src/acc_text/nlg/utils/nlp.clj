(ns acc-text.nlg.utils.nlp
  (:require [clojure.string :as str]))

(defn split-into-sentences [s]
  (str/split s #"(?<!\w\.\w.)(?<![A-Z][a-z]\.)(?<=\.|\?)\s"))

(defn tokenize [s]
  (map first (re-seq #"([\w+'`]+|[^\s\w+'`]+)" s)))

(defn word? [s]
  (some? (re-seq #"\w" s)))

(defn token-type [token] (if (word? token) "WORD" "PUNCTUATION"))

(defn capitalize-first-word [[head & tail]]
  (str/join [(str/capitalize head) (apply str tail)]))

(defn wrap-sentence [s]
  (str (str/trim s) "."))

(defn process-sentence [s]
  (if-not (str/blank? s)
    (wrap-sentence
     (capitalize-first-word s))
    ""))

(defn rebuild-sentence [tokens]
  (apply str
         (map (fn [{type :type text :text}]
                (case type
                  :WORD (str text " ")
                  :PUNCTUATION text)) tokens)))
