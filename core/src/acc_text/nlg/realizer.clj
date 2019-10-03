(ns acc-text.nlg.realizer
  (:require [acc-text.nlg.utils :as utils]
            [clojure.tools.logging :as log])
  (:import opennlp.ccg.grammar.Grammar
           [opennlp.ccg.hylo HyloHelper Nominal]
           [opennlp.ccg.realize Chart Realizer]
           [opennlp.ccg.synsem Category Sign]))

(defn convert-lf
  "Converts Sign LF to proper one for realization"
  [^Sign sign]
  (let [cat (.getCategory sign)
        index (.getIndexNominal cat)]
    (log/tracef "Category: %s Index: %s " cat index)
    (HyloHelper/compactAndConvertNominals (.getLF cat) index sign)))

(defn realize-sign
  "Realize sign into possible variants"
  [^Grammar grammar ^Sign sign]
  (log/tracef "Got sign to realize: '%s'" (utils/sign->bracket-str sign))
  (let [realizer (Realizer. grammar)
        lf (log/spyf :trace "Result after LF convertion: %s" (convert-lf sign))
        _ (.realize realizer lf)
        chart (.getChart realizer)
        edges (.bestEdges chart)]
    (map #(.getSign %) edges)))
