(ns acc-text.nlg.gf.modifiers
  (:require [acc-text.nlg.gf.utils :as utils]
            [clojure.java.io :as io]))

(defn load-modifiers [resource-path]
  (utils/read-edn (io/file (io/resource resource-path))))

(def modifier-map
  {"Eng" (load-modifiers "rgl/modifiers/eng.edn")
   "Est" (load-modifiers "rgl/modifiers/est.edn")
   "Ger" (load-modifiers "rgl/modifiers/ger.edn")
   "Lav" (load-modifiers "rgl/modifiers/lav.edn")
   "Rus" (load-modifiers "rgl/modifiers/rus.edn")
   "Spa" (load-modifiers "rgl/modifiers/spa.edn")})
