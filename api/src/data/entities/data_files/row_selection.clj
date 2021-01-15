(ns data.entities.data-files.row-selection
    (:require [clojure.set :as set]
              [clojure.tools.logging :as log]
              [data.utils :refer [murmur-hash]]))

(defn hash-row [row]
  (map-indexed (fn [idx k] (murmur-hash (str idx ":" k))) row))

(defn jaccard-distance [d1 d2]
  (if (not= d1 d2)
    (let [k1 (set d1)
          k2 (set d2)]
      (- 1 (/ (count (set/intersection k1 k2)) (count (set/union k1 k2)))))
    0))

(defn distance-matrix [rows]
  (let [hashed-rows (map (fn [row] (hash-row row)) rows)]
    (into
     {}
     (map-indexed
      (fn [id1 r1]
        [id1 (into {} (remove
                       (fn [[idx _]] (= id1 idx))
                       (map-indexed (fn [id2 r2] [id2 (jaccard-distance r1 r2)]) hashed-rows)))])
      hashed-rows))))

(defn select-rows [m rows limit]
  (loop [results [0]
         next    0]
    (if (or (= limit (count results)) (= (count results) (count rows)))
      (do
        (log/debugf "Result incides: %s" results)
        (map (fn [r] (nth rows r)) results))

      (let [too-close?     (fn [[_ x]] (< x (/ 1 10)))
            available-rows (remove (fn [[idx _]] (contains? (set results) idx)) (get m next)) ;; Rows which are not yet in result
            distant-rows   (remove (fn [[idx _]]  ;; Rows who are far enough from previous results
                                     (some too-close? (-> (get m idx) (select-keys (take-last 3 results)))))
                                   available-rows)
            k              (key (apply max-key val (if (empty? distant-rows) available-rows distant-rows)))]
        (recur (conj results k) k)))))


(defn sample [col limit]
  (let [l    (count col)
        step (- (/ l limit) 1)]
    (loop [[head & tail]   col
           result          []]
      (if (= (count result) limit)
        result
        (recur (drop step tail) (conj result head))))))