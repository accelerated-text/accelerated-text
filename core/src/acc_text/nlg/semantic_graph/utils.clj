(ns acc-text.nlg.semantic-graph.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.set :as set]))

(defn find-children [{relations ::sg/relations} ids]
  (let [relation-map (group-by ::sg/from relations)]
    (loop [ids ids child-ids #{}]
      (if-not (seq ids)
        child-ids
        (let [direct-child-ids (->> ids (mapcat relation-map) (map ::sg/to) (into #{}))]
          (recur direct-child-ids (set/union child-ids direct-child-ids)))))))

(defn prune-branches [semantic-graph ids]
  (let [ids-incl-children (set/union (set ids) (find-children semantic-graph ids))]
    (-> semantic-graph
        (update ::sg/concepts (fn [concepts]
                                (remove #(contains? ids-incl-children (::sg/id %))
                                        concepts)))
        (update ::sg/relations (fn [relations]
                                 (remove #(contains? ids-incl-children (::sg/to %))
                                         relations))))))
