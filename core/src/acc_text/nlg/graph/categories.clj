(ns acc-text.nlg.graph.categories
  (:require [acc-text.nlg.gf.paths :as paths]
            [acc-text.nlg.graph.utils :as utils]
            [loom.alg :as alg]
            [loom.graph :as graph]))

(defn set-category [g node-id category]
  (reduce (fn [g node-or-edge-id]
            (let [{current-category :category type :type} (get-in g [:attrs node-or-edge-id])]
              (cond-> g
                      (and (nil? current-category) (not (contains? #{:quote :data :dictionary-item} type)))
                      (assoc-in [:attrs node-or-edge-id :category] category))))
          g
          (cons node-id (map :id (graph/out-edges g node-id)))))

(defn get-in-categories [g node-id]
  (set (remove nil? (map #(get-in g [:attrs (:id %) :category]) (graph/in-edges g node-id)))))

(defn resolve-categories [g]
  (reduce (fn [g node-id]
            (let [categories (get-in-categories g node-id)]
              (case (count categories)
                0 g
                1 (set-category g node-id (first categories))
                (if-let [intersection (paths/get-intersection categories)]
                  (set-category g node-id intersection)
                  (throw (Exception. (format "Ambiguous categories for id `%s`" node-id)))))))
          g
          (alg/pre-traverse g (utils/find-root-id g))))
