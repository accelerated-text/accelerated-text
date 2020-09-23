(ns api.nlg.enrich.data
  (:require [api.nlg.enrich.data.transformations]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log])
  (:import (java.io PushbackReader)))

(defn enable-enrich? []
  (Boolean/valueOf ^String (or (System/getenv "DATA_ENRICH") "TRUE")))

(defn read-rules []
  (with-open [r (io/reader (io/resource "data/enrich.edn"))]
    (edn/read {:readers {'regex re-pattern}} (PushbackReader. r))))

(defn select-rules [filename rules]
  (filter #(or
             (and (some? (:filename %)) (= filename (:filename %)))
             (and (some? (:filename-pattern %)) (re-matches (:filename-pattern %) filename)))
          rules))

(defn build-transformations [tr]
  (apply comp (map (fn [{:keys [function args]}]
                     #((resolve (symbol function)) % args))
                   (reverse tr))))

(defn update-columns [data rules]
  (reduce (fn [data {columns :columns}]
            (reduce (fn [data {:keys [name replace transformations]}]
                      (if-let [val (get data name)]
                        (if (true? replace)
                          (let [tr-fn (build-transformations transformations)
                                result (tr-fn val)]
                            (log/debugf "`%s` -> `%s`" val result)
                            (assoc data name result)))
                        data))
                    data
                    columns))
          data
          rules))

(defn enrich [filename data]
  (log/info "Enriching data row...")
  (let [rules (select-rules filename (read-rules))]
    (log/infof "%d rules matches filename `%s`" (count rules) filename)
    (if (seq rules)
      (do
        (-> data (update-columns rules)))
      data)))
