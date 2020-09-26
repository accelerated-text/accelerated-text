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
                     (let [tr-fn (resolve (symbol function))]
                       #(tr-fn % args)))
                   (reverse tr))))

(defn update-fields [data fields]
  (reduce (fn [data {:keys [name transformations]}]
            (if (contains? data name)
              (let [tr-fn (build-transformations transformations)]
                (update data name tr-fn))
              (do
                (log/warn "Field `%s` was not found in data" name)
                data)))
          data
          fields))

(defn apply-rules [data rules]
  (reduce (fn [data {fields :fields}]
            (-> data
                (update-fields fields)))
          data
          rules))

(defn enrich [filename data]
  (log/info "Enriching data...")
  (let [rules (select-rules filename (read-rules))]
    (log/infof "%d rules matches filename `%s`" (count rules) filename)
    (cond-> data
            (seq rules) (apply-rules rules))))
