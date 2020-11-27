(ns api.nlg.enrich.data.transformations
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [numberwords.core :as nw]))

(defn string->num [^String n]
  (if (re-find #"[.]" n) (Float/valueOf n) (Integer/valueOf n)))

(defn number-approximation
  "Using Number Words package turn a number to its numeric expression"
  [s {:keys [language scale relation formatting]
      :or   {language   :en
             scale      10
             relation   :numberwords.domain/around
             formatting :numberwords.domain/bites}}]
  (when (some? s)
    (try
      (nw/numeric-expression (string->num s) scale language relation formatting)
      (catch Exception e
        (log/warnf "Failed number approximation of `%s`: %s" s (.getMessage e))
        s))))

(defn add-symbol
  "Add extra symbol to the front or the back of the value. Useful to add measurements or currency symbols"
  [s {:keys [symbol position skip] :or {position :back}}]
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

(defn cleanup
  "Cleanup the string using clojure.string/replace"
  [s {:keys [regex replacement]}] (str/replace s regex replacement))
