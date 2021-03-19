(ns acc-text.nlg.gf.paths
  (:require [acc-text.nlg.gf.utils :as utils]
            [clojure.java.io :as io]
            [clojure.set :as set]))

(defn load-paths [resource-path]
  (utils/read-edn (io/file (io/resource resource-path))))

(def path-map
  {"Eng" (load-paths "rgl/paths/eng.edn")
   "Est" (load-paths "rgl/paths/est.edn")
   "Ger" (load-paths "rgl/paths/ger.edn")
   "Lav" (load-paths "rgl/paths/lav.edn")
   "Rus" (load-paths "rgl/paths/rus.edn")
   "Spa" (load-paths "rgl/paths/spa.edn")})

(def possible-paths
  (->> (vals path-map)
       (mapcat keys)
       (group-by second)
       (reduce-kv (fn [m k v]
                    (assoc m k (set (map first v))))
                  {})))

(defn get-intersection [categories]
  (->> categories
       (map #(set (conj (get possible-paths %) %)))
       (apply set/intersection)
       (some #(when (contains? (set categories) %)
                %))))
