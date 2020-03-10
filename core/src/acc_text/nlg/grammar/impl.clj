(ns acc-text.nlg.grammar.impl
  (:require [acc-text.nlg.graph.utils :refer [attach-amrs find-root-id]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [clojure.string :as str]
            [loom.alg :refer [pre-traverse]]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]))

(def data-types #{:data :quote :dictionary-item})

(defn escape-string [s]
  (str/replace s #"\"" "\\\\\""))

(defn node->cat [graph node-id]
  (let [{:keys [type position]} (attrs graph node-id)]
    (str (->> (str/split (name type) #"-")
              (map str/capitalize)
              (str/join))
         (format "%02d" (or position 0)))))

(defn get-in-edge [graph node-id]
  (first (graph/in-edges graph node-id)))

(defn get-successors [graph node-id]
  (->> (graph/successors graph node-id)
       (sort-by (fn [successor-id]
                  (:index (attrs graph (get-in-edge graph successor-id)))))))

(defn remove-data-types [graph node-ids]
  (remove #(contains? data-types (:type (attrs graph %))) node-ids))

(defmulti build-node (fn [graph node-id] (:type (attrs graph node-id))))

(defmethod build-node :default [graph node-id]
  (let [successors (get-successors graph node-id)
        cat (node->cat graph node-id)]
    {:cat    [cat]
     :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
     :lincat {cat "Text"}
     :lin    {cat (str/join " | " (map #(node->cat graph %) successors))}}))

(defmethod build-node :operation [graph node-id]
  (let [{:keys [name module]} (attrs graph node-id)
        successors (get-successors graph node-id)
        cat (node->cat graph node-id)]
    {:cat    [cat]
     :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
     :lincat {cat (or (:category (attrs graph (get-in-edge graph node-id))) "Text")}
     :lin    {cat (cond-> (str module "." name)
                          (seq successors) (str " " (str/join " " (map #(node->cat graph %) successors))))}}))

(defmethod build-node :quote [graph node-id]
  (let [cat (node->cat graph node-id)]
    {:oper   [[cat "Str" (format "\"%s\"" (escape-string (:value (attrs graph node-id))))]]}))

(defn build-grammar
  ([semantic-graph context]
   (build-grammar "Default" "Instance" semantic-graph context))
  ([module instance semantic-graph context]
   (let [graph (-> semantic-graph (semantic-graph->ubergraph) (attach-amrs context))
         start-id (find-root-id graph)]
     (reduce (fn [grammar node-id]
               (merge-with (fn [acc val]
                             (cond
                               (map? acc) (merge acc val)
                               (coll? acc) (concat acc val)))
                           grammar
                           (build-node graph node-id)))
             {:module   module
              :instance instance
              :flags    {:startcat (node->cat graph start-id)}
              :cat      []
              :fun      {}
              :lincat   {}
              :lin      {}
              :oper     []}
             (pre-traverse graph start-id)))))
