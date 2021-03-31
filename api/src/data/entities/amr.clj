(ns data.entities.amr
  (:require [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [api.nlg.parser :refer [document-plan->semantic-graph]]
            [data.entities.document-plan :as dp]
            [data.entities.user-group :as user-group]))

(defn get-amr [id]
  (some-> id (dp/get-document-plan) (document-plan->semantic-graph)))

(defn list-amrs [group-id]
  (map document-plan->semantic-graph (dp/list-document-plans "AMR" group-id)))

(defn list-rgls [group-id]
  (map document-plan->semantic-graph (dp/list-document-plans "RGL" group-id)))

(defn find-amrs [semantic-graph]
  (loop [amr-ids (sg-utils/get-amr-ids semantic-graph)
         output '()]
    (if-not (seq amr-ids)
      (vec output)
      (let [amrs (map get-amr amr-ids)]
        (recur
          (remove nil? (mapcat sg-utils/get-amr-ids amrs))
          (concat output amrs))))))
