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

(defn get-child-with-relation [{::sg/keys [concepts relations]} {id ::sg/id} role]
  (let [concept-map (zipmap (map ::sg/id concepts) concepts)
        relation-map (group-by ::sg/from relations)]
    (->> (get relation-map id)
         (some #(when (= role (::sg/role %)) %))
         (::sg/to)
         (get concept-map))))

(defn get-children [{::sg/keys [concepts relations]} {id ::sg/id}]
  (let [concept-map (zipmap (map ::sg/id concepts) concepts)
        relation-map (group-by ::sg/from relations)]
    (map #(get concept-map (::sg/to %)) (get relation-map id))))

(defn get-concepts-with-type [{concepts ::sg/concepts} type]
  (filter #(= type (::sg/type %)) concepts))

(defn prune-branches [semantic-graph ids]
  (let [ids-incl-descendants (set/union (set ids) (find-descendant-ids semantic-graph ids))]
    (-> semantic-graph
        (update ::sg/concepts (fn [concepts]
                                (remove #(contains? ids-incl-descendants (::sg/id %))
                                        concepts)))
        (update ::sg/relations (fn [relations]
                                 (remove #(contains? ids-incl-descendants (::sg/to %))
                                         relations))))))

(defn prune-unrelated-branches [{::sg/keys [concepts relations] :as semantic-graph}]
  (prune-branches semantic-graph (set/difference (into #{} (map ::sg/id (rest concepts)))
                                                 (into #{} (map ::sg/to relations)))))
