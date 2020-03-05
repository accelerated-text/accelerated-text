(ns acc-text.nlg.semantic-graph.utils
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [ubergraph.core :as uber]))

(defn find-concept-ids [{concepts ::sg/concepts} types]
  (->> concepts
       (filter #(contains? (set types) (:type %)))
       (map :id)
       (into #{})))

(defn find-data-predicate-concept-ids [{concepts ::sg/concepts relations ::sg/relations}]
  (let [concepts-under-predicate (->> relations
                                      (filter #(= :predicate (:role %)))
                                      (map #(:to %)))]
    (->> concepts
         (filter #(= :data (:type %)))
         (map :id)
         (filter #(contains? (set concepts-under-predicate) %))))) 

(defn find-child-ids [{relations ::sg/relations} ids]
  (let [relation-map (group-by :from relations)]
    (->> ids
         (mapcat relation-map)
         (map :to)
         (into #{}))))

(defn find-descendant-ids [{relations ::sg/relations} ids]
  (let [relation-map (group-by :from relations)]
    (loop [ids ids descendant-ids #{}]
      (if-not (seq ids)
        descendant-ids
        (let [child-ids (->> ids (mapcat relation-map) (map :to) (into #{}))]
          (recur child-ids (set/union descendant-ids child-ids)))))))

(defn get-child-with-relation [{::sg/keys [concepts relations]} {id :id} role]
  (let [concept-map (zipmap (map :id concepts) concepts)
        relation-map (group-by :from relations)]
    (->> (get relation-map id)
         (some #(when (= role (:role %)) %))
         (:to)
         (get concept-map))))

(defn get-children [{::sg/keys [concepts relations]} {id :id}]
  (let [concept-map (zipmap (map :id concepts) concepts)
        relation-map (group-by :from relations)]
    (map #(get concept-map (:to %)) (get relation-map id))))

(defn get-concepts-with-type [{concepts ::sg/concepts} type]
  (filter #(= type (:type %)) concepts))

(defn prune-branches [semantic-graph ids]
  (let [ids-incl-descendants (set/union (set ids) (find-descendant-ids semantic-graph ids))]
    (-> semantic-graph
        (update ::sg/concepts (fn [concepts]
                                (remove #(contains? ids-incl-descendants (:id %)) concepts)))
        (update ::sg/relations (fn [relations]
                                 (remove #(contains? ids-incl-descendants (:to %)) relations))))))

(defn prune-concepts-by-type [semantic-graph type]
  (->> (get-concepts-with-type semantic-graph type)
       (map :id)
       (into #{})
       (prune-branches semantic-graph)))

(defn prune-nil-relations [semantic-graph]
  (update semantic-graph ::sg/relations (fn [relations]
                                          (remove (fn [{:keys [from to]}]
                                                    (or (nil? from) (nil? to)))
                                                  relations))))

(defn prune-unrelated-branches [{::sg/keys [concepts relations] :as semantic-graph}]
  (prune-branches semantic-graph (set/difference (into #{} (map :id (rest concepts)))
                                                 (into #{} (map :to relations)))))

(defn select-pairs [pairs nodes]
  (map (fn [[start end]]
         (log/debugf "Searching for pair: %s %s" start end)
         {:start  (first (filter (fn [{:keys [to]}] (= to start)) nodes))
          :middle (first (filter (fn [{:keys [from to]}] (and (= from start) (= to end))) nodes))
          :end    (first (filter (fn [{:keys [from]}] (= from end)) nodes))})
       pairs))

(defn select-related-nodes [ids-for-removal relations]
  (let [pairs (zipmap ids-for-removal (rest ids-for-removal))]
    (->> relations
         (select-pairs pairs)
         (remove #(nil? (:middle %))))))

 (defn remove-condition-nodes [semantic-graph]
  (let [ids-for-removal (->> semantic-graph
                             ::sg/concepts
                             (filter #(contains? #{:condition :if-statement :default-statement} (:type %)))
                             (map :id)
                             (distinct)
                             (vec))]
    (log/debugf "Ids for removal: %s" (pr-str ids-for-removal))
    (-> semantic-graph
        (update ::sg/concepts (fn [concepts]
                                (remove #(.contains ids-for-removal (:id %)) concepts)))
        (update ::sg/relations (fn [relations]
                                 (->> relations
                                      (group-by (fn [{:keys [from to]}] (or (.contains ids-for-removal from)
                                                                            (.contains ids-for-removal to))))
                                      (map (fn [[conditional? values]]
                                             (if conditional?
                                               (->> values
                                                    (select-related-nodes ids-for-removal)
                                                    (map (fn [{:keys [start end]}] (assoc start :to (:to end)))))
                                               values)))
                                      (apply concat)
                                      (sort-by :from)))))))

(defn node-name [{:keys [id type value]}]
  (if (= type :quote)
    value
    (-> "%s.%s %s"
        (format (name id) (name type) (str value))
        (str/trim)
        (keyword))))

(defn plan-graph [{::sg/keys [concepts relations]}]
  (let [concepts (zipmap (map :id concepts) concepts)]
    (apply uber/graph (map (fn [{:keys [from to role]}]
                             [(node-name (get concepts from))
                              (node-name (get concepts to))
                              {:name role}])
                           relations))))

(defn vizgraph [semantic-graph] (uber/viz-graph (plan-graph semantic-graph)))

(def boolean-strings
  {"true"  true
   "false" false
   "yes"   true
   "no"    false
   "1"     true
   "0"     false})
(defn is-boolean-string? [value] (->> (str/lower-case value) (contains? (set (keys boolean-strings)))))
(defn eval-boolean-string [value] (->> (str/lower-case value) (get boolean-strings)))
