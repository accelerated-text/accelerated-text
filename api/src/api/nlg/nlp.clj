(ns api.nlg.nlp
  (:require [clojure.string :as str]))

(defn split-into-sentences [s]
  (str/split s #"(?<!\w\.\w.)(?<![A-Z][a-z]\.)(?<=\.|\?)\s"))

(defn tokenize [s]
  (map first (re-seq #"([\w+'`]+|[^\s\w+'`]+)" s)))

(defn word? [s]
  (some? (re-seq #"\w" s)))

(defn token-type [token] (if (word? token) "WORD" "PUNCTUATION"))
