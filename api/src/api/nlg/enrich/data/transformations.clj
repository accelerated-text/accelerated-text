(ns api.nlg.enrich.data.transformations
  (:require [clojure.string :as str]
            [numberwords.core :as nw]))

(defn num->string [n]
  (if (re-find #"[.,]" n) (Float/valueOf n) (Integer/valueOf n)))

(defn number-approximation [s {:keys [language scale relation formatting]
                               :or   {language   :en
                                      relation   :numberwords.domain/around
                                      formatting :numberwords.domain/bites}}]
  (if (str/blank? s)
    ""
    (nw/numeric-expression (num->string s) scale language relation formatting)))

(defn add-symbol [s {:keys [symbol position skip] :or {position :back}}]
  (if (some? symbol)
    (let [n-chars-to-skip (if (seq skip)
                            (-> (format "%s[%s]+%s"
                                        (if (= :front position) "^" "")
                                        (str/join skip)
                                        (if (= :back position) "$" ""))
                                (re-pattern)
                                (re-find s)
                                (count))
                            0)]
      (if (= :front position)
        (str (subs s 0 n-chars-to-skip) symbol (subs s n-chars-to-skip))
        (str (subs s 0 (- (count s) n-chars-to-skip)) symbol (subs s (- (count s) n-chars-to-skip)))))
    s))

(defn cleanup [s {:keys [regex replace]}] (str/replace s regex replace))
