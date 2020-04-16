(ns acc-text.nlg.graph.categories
  (:require [acc-text.nlg.graph.utils :as utils]
            [loom.graph :as graph]
            [loom.alg :as alg]))

(defn set-category [g node-id category]
  (->> (graph/successors g node-id)
       (cons node-id)
       (mapcat #(cons % (map :id (graph/out-edges g %))))
       (reduce (fn [g node-or-edge-id]
                 (let [{current-category :category type :type} (get-in g [:attrs node-or-edge-id])]
                   (cond-> g
                           (and (nil? current-category) (not (contains? #{:quote :data :dictionary-item} type)))
                           (assoc-in [:attrs node-or-edge-id :category] category))))
               g)))

(defn resolve-categories [g]
  (reduce (fn [g node-id]
            (let [categories (set (remove nil? (map #(get-in g [:attrs (:id %) :category]) (graph/in-edges g node-id))))]
              (case (count categories)
                0 g
                1 (set-category g node-id (first categories))
                (throw (Exception. (format "Ambiguous categories for id `%s`" node-id))))))
          g
          (alg/pre-traverse g (utils/find-root-id g))))
