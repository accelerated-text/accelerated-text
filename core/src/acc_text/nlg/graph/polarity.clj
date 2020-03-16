(ns acc-text.nlg.graph.polarity
  (:require [acc-text.nlg.graph.utils :refer [find-edges]]
            [acc-text.nlg.graph.condition :refer [string->boolean]]
            [loom.attr :refer [attrs]]))

(defn resolve-polarity [g]
  (reduce (fn [g [_ node-id _]]
            (let [{:keys [value position]} (attrs g node-id)]
              (assoc-in g [:attrs node-id] {:type     :operation
                                            :name     (if (string->boolean value)
                                                        "positivePol"
                                                        "negativePol")
                                            :module   "Syntax"
                                            :position position})))
          g
          (find-edges g {:category "Pol"})))
