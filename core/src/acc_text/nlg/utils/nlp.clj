(ns acc-text.nlg.utils.nlp
  (:require [clojure.string :as str]))

(defn split-into-sentences [s]
  (str/split s #"(?<!\w\.\w.)(?<![A-Z][a-z]\.)(?<=\.|\?)\s"))

(defn tokenize [s]
  (map first (re-seq #"([\w+'`]+|[^\s\w+'`]+)" s)))

(defn tokenize-incl-space [s]
  (map first (re-seq #"([\w+'`]+|[\s]+|[^\s\w+'`]+)" s)))

(defn word? [s]
  (some? (re-seq #"\w" s)))

(defn token-type [token] (if (word? token) "WORD" "PUNCTUATION"))

(defn process-sentence [s]
  (reduce str (when-not (str/blank? s)
                (str/join [(str/capitalize (first s)) (apply str (rest s)) \.]))))

(defn annotate [text]
  {:text   text
   :tokens (loop [[token & tokens] (tokenize-incl-space text)
                  idx 0
                  annotations []]
             (if (nil? token)
               annotations
               (recur tokens (+ idx (count token)) (cond-> annotations
                                                           (re-matches #"[^\s]+" token) (conj {:text token
                                                                                               :idx  idx})))))})
