(ns acc-text.nlg.grammar.impl
  (:require [acc-text.nlg.grammar.dictionary-item :refer [build-dictionary-item]]
            [acc-text.nlg.graph.amr :refer [attach-amrs]]
            [acc-text.nlg.graph.categories :refer [resolve-categories]]
            [acc-text.nlg.graph.condition :refer [determine-conditions]]
            [acc-text.nlg.graph.data :refer [resolve-data]]
            [acc-text.nlg.graph.dictionary-item :refer [resolve-dictionary-items]]
            [acc-text.nlg.graph.lists :refer [resolve-lists]]
            [acc-text.nlg.graph.modifier :refer [resolve-modifiers]]
            [acc-text.nlg.graph.paths :refer [resolve-paths]]
            [acc-text.nlg.graph.polarity :refer [resolve-polarity]]
            [acc-text.nlg.graph.utils :refer [add-concept-position find-root-id get-in-edge get-successors prune-graph]]
            [acc-text.nlg.graph.variables :refer [resolve-variables]]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [clojure.math.combinatorics :refer [permutations]]
            [clojure.string :as str]
            [loom.alg :refer [pre-traverse]]
            [loom.attr :refer [attrs]]))

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

(defmulti build-node (fn [graph node-id] (:type (attrs graph node-id))))

(defmethod build-node :default [graph node-id]
  (let [successors (get-successors graph node-id)
        category (get (attrs graph node-id) :category "Str")
        cat (node->cat graph node-id)]
    #:acc-text.nlg.grammar
        {:cat    [cat]
         :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
         :lincat {cat category}
         :lin    {cat [(str/join " ++ " (map #(cond-> (node->cat graph %)
                                                      (and
                                                        (= category "Str")
                                                        (s-node? graph %)) (str ".s"))
                                             successors))]}}))

(defmethod build-node :amr [graph node-id]
  (let [successors (get-successors graph node-id)
        category (get (attrs graph node-id) :category "Str")
        cat (node->cat graph node-id)]
    #:acc-text.nlg.grammar
        {:cat    [cat]
         :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
         :lincat {cat category}
         :lin    {cat [(str/join " | " (map #(cond-> (node->cat graph %)
                                                     (and
                                                       (= category "Str")
                                                       (s-node? graph %)) (str ".s"))
                                            successors))]}}))

(defmethod build-node :operation [graph node-id]
  (let [{:keys [name module]} (attrs graph node-id)
        successors (get-successors graph node-id)
        cat (node->cat graph node-id)]
    #:acc-text.nlg.grammar
        {:cat    [cat]
         :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
         :lincat {cat (or (:category (attrs graph (get-in-edge graph node-id))) "Text")}
         :lin    {cat [(cond-> (str module "." name)
                               (seq successors) (str " " (str/join " " (map #(node->cat graph %) successors))))]}}))

(defmethod build-node :quote [graph node-id]
  (let [{:keys [value]} (attrs graph node-id)
        cat (node->cat graph node-id)]
    #:acc-text.nlg.grammar
        {:oper [[cat "Str" (format "\"%s\"" (str/replace value #"\"" "\\\\\""))]]}))

(defmethod build-node :dictionary-item [graph node-id]
  (let [cat (node->cat graph node-id)
        in-edge-category (get-in graph [:attrs (:id (get-in-edge graph node-id)) :category])
        {category :category :as attrs} (attrs graph node-id)]
    #:acc-text.nlg.grammar
        {:oper [[cat
                 (cond
                   (= "Str" in-edge-category) "Str"
                   (= "Str" category) "{s : Str}"
                   (nil? in-edge-category) "Text"
                   :else category)
                 (build-dictionary-item in-edge-category attrs)]]}))

(defmethod build-node :synonyms [graph node-id]
  (let [successors (get-successors graph node-id)
        category (:category (attrs graph node-id))
        cat (node->cat graph node-id)]
    #:acc-text.nlg.grammar
        {:cat    [cat]
         :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
         :lincat {cat (or category "Str")}
         :lin    {cat (map #(cond-> (node->cat graph %)
                                    (and (s-node? graph %) (nil? category)) (str ".s"))
                           successors)}}))

(defmethod build-node :shuffle [graph node-id]
  (let [successors (get-successors graph node-id)
        category (:category (attrs graph node-id))
        cat (node->cat graph node-id)]
    #:acc-text.nlg.grammar
        {:cat    [cat]
         :fun    {cat (->> successors (remove-data-types graph) (map #(node->cat graph %)))}
         :lincat {cat (or category "Str")}
         :lin    {cat [(str/join " | " (->> (permutations successors)
                                            (remove empty?)
                                            (map (fn [group]
                                                   (->> (map #(cond-> (node->cat graph %)
                                                                      (and (s-node? graph %) (nil? category)) (str ".s"))
                                                             group)
                                                        (str/join " ++ ")
                                                        (format "(%s)"))))))]}}))

(defn ->graph [semantic-graph context]
  (-> semantic-graph
      (semantic-graph->ubergraph)
      (attach-amrs context)
      (resolve-variables)
      (resolve-categories)
      (determine-conditions context)
      (prune-graph)
      (resolve-dictionary-items context)
      (resolve-data context)
      (resolve-lists)
      (resolve-modifiers)
      (resolve-polarity)
      (resolve-paths context)
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
                           (build-node graph node-id)))
             #:acc-text.nlg.grammar
                 {:module   module
                  :instance instance
                  :flags    {:startcat (node->cat graph start-id)}
                  :cat      []
                  :fun      {}
                  :lincat   {}
                  :lin      {}
                  :oper     []}
             (pre-traverse graph start-id)))))
