(ns acc-text.nlg.gf.parser
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.string :as str]
            [clojure.zip :as zip]))

(defn get-attrs [cat]
  (when (some? cat)
    (let [[_ type position] (re-find #"(.+?)(\d+)" cat)]
      {:type     (keyword (str/replace type #"_" "-"))
       :position (Integer/parseInt position)})))

(defn build-semantic-graph [{:keys [id text cat children]}]
  #::sg{:relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :child})
                        children)
        :concepts  [(merge
                      {:id id}
                      (if (some? cat)
                        (get-attrs cat)
                        {:type  :quote
                         :value text}))]})

(defn preprocess [z]
  (loop [index 1
         z z]
    (if (zip/end? z)
      (zip/root z)
      (recur
        (inc index)
        (-> z
            (zip/edit #(some-> %
                               (cond->> (string? %) (hash-map :text))
                               (assoc :id (keyword (format "%02d" index)))))
            (zip/next))))))

(defn make-zipper [t]
  (zip/zipper
    map?
    (fn [node]
      (:children node))
    (fn [node children]
      (assoc node :children children))
    t))

(defn tree->semantic-graph [t]
  (loop [z (-> t (make-zipper) (preprocess) (make-zipper))
         graph #::sg{:relations []
                     :concepts  []}]
    (if (zip/end? z)
      graph
      (recur
        (zip/next z)
        (merge-with concat graph (build-semantic-graph (zip/node z)))))))
