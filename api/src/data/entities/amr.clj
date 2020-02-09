(ns data.entities.amr
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [data.entities.document-plan :as dp]))

(defn document-plan->amr [{:keys [id name documentPlan]}]
  (let [{::sg/keys [concepts relations] :as semantic-graph} (document-plan->semantic-graph documentPlan)]
    {:id             id
     :label          name
     :kind           "Str"
     :roles          (let [reference-ids (->> concepts
                                              (filter #(= :reference (:type %)))
                                              (map :id)
                                              (set))]
                       (->> relations
                            (filter #(contains? reference-ids (:to %)))
                            (map (fn [relation]
                                   {:type (get-in relation [:attributes :name])}))))
     :semantic-graph semantic-graph}))

(defn get-amr [id]
  (-> id
      (dp/get-document-plan)
      (document-plan->amr)))

(defn list-amrs []
  (map document-plan->amr (dp/list-document-plans "AMR")))
