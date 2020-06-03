(ns data.entities.amr
  (:require [acc-text.nlg.graph.categories :as categories]
            [acc-text.nlg.graph.utils :as graph-utils]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [clojure.set :as set]
            [data.entities.document-plan :as dp]))

(defn get-relation-categories [{relations ::sg/relations}]
  (reduce (fn [m {concept-id :to category :category}]
            (cond-> m
                    (some? category) (assoc concept-id category)))
          {}
          relations))

(defn find-unrelated-concepts [{::sg/keys [concepts relations]}]
  (set/difference (set (map :id concepts)) (set (map :from relations))))

(defn get-category [sg]
  (let [root (sg-utils/find-root sg)
        segments (sg-utils/get-children sg root)
        first-segment-blocks (sg-utils/get-children sg (first segments))
        first-block (first first-segment-blocks)]
    (or (when (and (= 1 (count first-segment-blocks)) (= :amr (:type first-block)))
          (:category first-block))
        "Str")))

(defn resolve-categories [semantic-graph]
  (-> semantic-graph
      (sg-utils/semantic-graph->ubergraph :keep-ids? true)
      (categories/resolve-categories)
      (graph-utils/ubergraph->semantic-graph :keep-ids? true)))

(defn document-plan->amr [{:keys [id name documentPlan examples] :as entity}]
  (let [metadata {:var-names (dp/get-variable-names entity)}
        {::sg/keys [description] :as semantic-graph} (document-plan->semantic-graph documentPlan metadata)
        {::sg/keys [concepts] :as semantic-graph} (resolve-categories semantic-graph)]
    {:id             id
     :label          name
     :kind           (get-category semantic-graph)
     :semantic-graph semantic-graph
     :frames         [{:examples (or examples (cond-> [] (some? description) (conj description)))}]
     :roles          (let [categories (get-relation-categories semantic-graph)
                           unrelated-concepts (find-unrelated-concepts semantic-graph)]
                       (loop [[reference & rs] (filter (fn [{:keys [id type]}]
                                                         (and (= :reference type) (contains? unrelated-concepts id)))
                                                       concepts)
                              index 0 vars #{} roles []]
                         (if-not (some? reference)
                           roles
                           (let [{:keys [id name]} reference]
                             (recur
                               rs
                               (inc index)
                               (conj vars name)
                               (cond-> roles
                                       (nil? (get vars name)) (conj {:id    (format "ARG%d" index)
                                                                     :label name
                                                                     :type  (get categories id)})))))))}))

(defn get-amr [id]
  (some-> id (dp/get-document-plan) (document-plan->amr)))

(defn get-amrs [semantic-graph]
  (loop [amr-ids (sg-utils/get-amr-ids semantic-graph)
         output '()]
    (if-not (seq amr-ids)
      (vec output)
      (let [amrs (map get-amr amr-ids)]
        (recur
          (remove nil? (mapcat (comp sg-utils/get-amr-ids :semantic-graph) amrs))
          (concat output amrs))))))

(defn list-amrs []
  (map document-plan->amr (dp/list-document-plans "AMR")))

(defn list-rgls []
  (map document-plan->amr (dp/list-document-plans "RGL")))
