(ns acc-text.nlg.gf.paths
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.gf.utils :as utils]
            [clojure.java.io :as io]))

(defn path->sg [path]
  (letfn [(->id [index] (keyword (format "%02d" (inc index))))]
    #::sg{:relations (map-indexed (fn [index [_ _ category]]
                                    {:from     (->id index)
                                     :to       (->id (inc index))
                                     :role     :arg
                                     :index    0
                                     :category category})
                                  (rest path))
          :concepts  (map-indexed (fn [index [module name _]]
                                    {:id     (->id index)
                                     :type   :operation
                                     :name   name
                                     :module module})
                                  path)}))

(defn load-paths [resource-path]
  (reduce-kv (fn [m k v]
               (assoc m k (path->sg (first v))))
             {}
             (utils/read-edn (io/file (io/resource resource-path)))))

(def path-map
  {"Eng" (load-paths "rgl/paths/eng.edn")
   "Est" (load-paths "rgl/paths/est.edn")
   "Ger" (load-paths "rgl/paths/ger.edn")
   "Lav" (load-paths "rgl/paths/lav.edn")
   "Rus" (load-paths "rgl/paths/rus.edn")
   "Spa" (load-paths "rgl/paths/spa.edn")})
