(ns data.entities.amr
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [data.entities.document-plan :as dp]))

(defn get-relation-names [{relations ::sg/relations}]
  (reduce (fn [m {concept-id :to {name :name} :attributes}]
            (cond-> m
                    (some? name) (assoc concept-id name)))
          {}
          relations))

(defn document-plan->amr [{:keys [id name documentPlan] :as entity}]
  (let [{concepts ::sg/concepts :as semantic-graph} (document-plan->semantic-graph
                                                      documentPlan
                                                      {:var-names (dp/get-variable-names entity)})]
    {:id             id
     :label          name
     :kind           "Str"
     :semantic-graph semantic-graph
     :roles          (let [relation-names (get-relation-names semantic-graph)]
                       (loop [[reference & rs] (filter #(= :reference (:type %)) concepts)
                              index 0 vars #{} roles []]
                         (if-not (some? reference)
                           roles
                           (let [{id :id {name :name} :attributes} reference]
                             (recur
                               rs
                               (inc index)
                               (conj vars name)
                               (cond-> roles
                                       (nil? (get vars name)) (conj {:id    (format "ARG%d" index)
                                                                     :type  (get relation-names id)
                                                                     :label name})))))))}))

(defn get-amr [id]
  (-> id
      (dp/get-document-plan)
      (document-plan->amr)))

(defn list-amrs []
  (map document-plan->amr (dp/list-document-plans "AMR")))

(defn list-rgls []
  (map document-plan->amr (dp/list-document-plans "RGL")))
