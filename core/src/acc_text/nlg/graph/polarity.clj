(ns acc-text.nlg.graph.polarity
  (:require [acc-text.nlg.graph.utils :refer [find-edges]]
            [acc-text.nlg.graph.condition :refer [string->boolean]]
            [loom.attr :refer [attrs]]))

(defn resolve-polarity [g]
  (reduce (fn [g [_ node-id _]]
            (let [{:keys [value name]} (attrs g node-id)]
              (assoc-in g [:attrs node-id] {:type   :operation
                                            :module "Syntax"
                                            :name   (cond
                                                      (contains? #{"positivePol" "negativePol"} name) name
                                                      (true? (string->boolean value)) "positivePol"
                                                      (false? (string->boolean value)) "negativePol")})))
          g
          (find-edges g {:category "Pol"})))
