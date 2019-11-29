(ns acc-text.nlg.semantic-graph.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.set :as set]))

(defn find-concept-ids [{concepts ::sg/concepts} types]
  (->> concepts
       (filter #(contains? (set types) (::sg/type %)))
       (map ::sg/id)
       (into #{})))

(defn find-child-ids [{relations ::sg/relations} ids]
  (let [relation-map (group-by ::sg/from relations)]
    (->> ids
         (mapcat relation-map)
         (map ::sg/to)
         (into #{}))))

(defn find-descendant-ids [{relations ::sg/relations} ids]
  (let [relation-map (group-by ::sg/from relations)]
    (loop [ids ids descendant-ids #{}]
      (if-not (seq ids)
        descendant-ids
        (let [child-ids (->> ids (mapcat relation-map) (map ::sg/to) (into #{}))]
          (recur child-ids (set/union descendant-ids child-ids)))))))

(defn prune-branches [semantic-graph ids]
  (let [ids-incl-children (set/union (set ids) (find-descendant-ids semantic-graph ids))]
    (-> semantic-graph
        (update ::sg/concepts (fn [concepts]
                                (remove #(contains? ids-incl-children (::sg/id %))
                                        concepts)))
        (update ::sg/relations (fn [relations]
                                 (remove #(contains? ids-incl-children (::sg/to %))
                                         relations))))))
