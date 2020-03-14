(ns acc-text.nlg.grammar.impl
  (:require [acc-text.nlg.grammar.dictionary-item :refer [build-dictionary-item]]
            [acc-text.nlg.grammar.data-item :refer [build-data-item]]
            [acc-text.nlg.grammar.utils :refer [escape-string]]
            [acc-text.nlg.graph.amr :refer [attach-amrs]]
            [acc-text.nlg.graph.condition :refer [determine-conditions]]
            [acc-text.nlg.graph.data :refer [resolve-data]]
            [acc-text.nlg.graph.dictionary-item :refer [resolve-dictionary-items]]
            [acc-text.nlg.graph.lists :refer [resolve-lists]]
            [acc-text.nlg.graph.modifier :refer [resolve-modifiers]]
            [acc-text.nlg.graph.polarity :refer [resolve-polarity]]
            [acc-text.nlg.graph.utils :refer [find-root-id get-successors get-in-edge add-concept-position prune-graph]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [clojure.string :as str]
            [loom.alg :refer [pre-traverse]]
            [loom.attr :refer [attrs]]
            [clojure.tools.logging :as log]))

(def data-types #{:data :quote :dictionary-item})

(defn node->cat [graph node-id]
  (let [{:keys [type position]} (attrs graph node-id)]
    (str (->> (str/split (name type) #"-")
              (map str/capitalize)
              (str/join))
         (format "%02d" (or position 0)))))

(defn s-node? [graph node-id]
  (contains? #{:dictionary-item :operation} (:type (attrs graph node-id))))

(defn remove-data-types [graph node-ids]
  (remove #(contains? data-types (:type (attrs graph %))) node-ids))

(defmulti build-node (fn [graph node-id _] (:type (attrs graph node-id))))

(defmethod build-node :default [graph node-id _]
  (let [successors (get-successors graph node-id)
        cat (node->cat graph node-id)]
    {:cat    [cat]
     :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
     :lincat {cat "Str"}
     :lin    {cat [(str/join " ++ " (map #(cond-> (node->cat graph %)
                                                  (s-node? graph %) (str ".s"))
                                         successors))]}}))

(defmethod build-node :frame [graph node-id _]
  (let [successors (get-successors graph node-id)
        cat (node->cat graph node-id)]
    {:cat    [cat]
     :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
     :lincat {cat "Str"}
     :lin    {cat [(str/join " | " (map #(cond-> (node->cat graph %)
                                                 (s-node? graph %) (str ".s"))
                                        successors))]}}))

(defmethod build-node :operation [graph node-id _]
  (let [{:keys [name module]} (attrs graph node-id)
        successors (get-successors graph node-id)
        cat (node->cat graph node-id)]
    {:cat    [cat]
     :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
     :lincat {cat (or (:category (attrs graph (get-in-edge graph node-id))) "Text")}
     :lin    {cat [(cond-> (str module "." name)
                           (seq successors) (str " " (str/join " " (map #(node->cat graph %) successors))))]}}))

(defmethod build-node :quote [graph node-id _]
  (let [cat (node->cat graph node-id)]
    {:oper [[cat "Str" (format "\"%s\"" (escape-string (:value (attrs graph node-id))))]]}))

(defmethod build-node :data [graph node-id lang]
  {:oper [[(node->cat graph node-id)
           "N" ;;FIXME sometimes data can be used as modifier, and then it has to be "A"
           (build-data-item (escape-string (:value (attrs graph node-id))) lang)]]})

(defmethod build-node :dictionary-item [graph node-id _]
  (let [cat (node->cat graph node-id)
        in-edge-category (get-in graph [:attrs (:id (get-in-edge graph node-id)) :category])
        {category :category :as attrs} (attrs graph node-id)]
    {:oper [[cat category (build-dictionary-item in-edge-category attrs)]]}))

(defmethod build-node :synonyms [graph node-id _]
  (let [successors (get-successors graph node-id)
        category (:category (attrs graph node-id))
        cat (node->cat graph node-id)]
    {:cat    [cat]
     :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
     :lincat {cat (or category "Str")}
     :lin    {cat (map #(cond-> (node->cat graph %)
                                (and (s-node? graph %) (nil? category)) (str ".s"))
                       successors)}}))

(defn ->graph [semantic-graph context]
  (-> semantic-graph
      (semantic-graph->ubergraph)
      (attach-amrs context)
      (determine-conditions context)
      (prune-graph)
      (resolve-data context)
      (resolve-polarity)
      (resolve-dictionary-items context)
      (resolve-lists)
      (resolve-modifiers)
      (add-concept-position)))

(defn build-grammar
  ([semantic-graph context]
   (build-grammar "Default" "Instance" semantic-graph context))
  ([module instance semantic-graph context]
   (let [graph (->graph semantic-graph context)
         start-id (find-root-id graph)]
     (reduce (fn [grammar node-id]
               (merge-with (fn [acc val]
                             (cond
                               (map? acc) (merge acc val)
                               (coll? acc) (concat acc val)))
                           grammar
                           (build-node graph node-id (get-in context [:constants "*Language"]))))
             {:module   module
              :instance instance
              :flags    {:startcat (node->cat graph start-id)}
              :cat      []
              :fun      {}
              :lincat   {}
              :lin      {}
              :oper     []}
             (pre-traverse graph start-id)))))
