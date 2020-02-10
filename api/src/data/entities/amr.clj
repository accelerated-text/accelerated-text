(ns data.entities.amr
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [data.entities.document-plan :as dp]))

(defn document-plan->amr [{:keys [id name documentPlan]}]
  (let [{::sg/keys [concepts relations] :as semantic-graph} (document-plan->semantic-graph documentPlan)]
    {:id             id
     :label          name
     :kind           "Str"
     :roles          (let [concept-names (reduce (fn [m {to :to {name :name} :attributes}]
                                                   (cond-> m
                                                           (some? name) (assoc to name)))
                                                 {}
                                                 relations)]
                       (loop [[reference & rs] (filter #(= :reference (:type %)) concepts)
                              index 0
                              names #{}
                              roles []]
                         (if-not (some? reference)
                           roles
                           (let [{id :id {name :name} :attributes} reference]
                             (recur
                               rs
                               (inc index)
                               (conj names name)
                               (cond-> roles
                                       (not (contains? names name)) (conj {:id   (format "ARG%d" index)
                                                                           :type (get concept-names id)})))))))
     :semantic-graph semantic-graph}))

(defn get-amr [id]
  (-> id
      (dp/get-document-plan)
      (document-plan->amr)))

(defn list-amrs []
  (map document-plan->amr (dp/list-document-plans "AMR")))

(defn list-rgls []
  (map document-plan->amr (dp/list-document-plans "RGL")))
