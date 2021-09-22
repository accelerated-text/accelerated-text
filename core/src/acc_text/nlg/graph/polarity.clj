(ns acc-text.nlg.graph.polarity
  (:require [acc-text.nlg.graph.utils :refer [find-edges]]
            [acc-text.nlg.graph.condition :refer [string->boolean]]
            [loom.attr :refer [attrs]]))

(defn resolve-polarity [g]
  (reduce (fn [g [_ node-id _]]
            (let [{:keys [value type]} (attrs g node-id)]
              (cond-> g
                (= :quote type) (assoc-in [:attrs node-id] {:type   :operation
                                                            :module "Syntax"
                                                            :name   (if (true? (string->boolean value))
                                                                      "positivePol"
                                                                      "negativePol")}))))
          g
          (find-edges g {:category "Pol"})))
