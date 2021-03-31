(ns acc-text.nlg.graph.lists
  (:require [acc-text.nlg.graph.utils :as gut]
            [acc-text.nlg.graph.modifier :refer [find-category]]
            [acc-text.nlg.gf.paths :refer [path-map]]
            [clojure.math.combinatorics :refer [permutations]]
            [ubergraph.core :as uber]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [loom.alg :as alg])
  (:import java.util.UUID))

(defn add-node [g node attrs]
  (uber/add-nodes-with-attrs g [node attrs]))

(defn attrs-from-edge [g from to]
  (uber/attrs g (uber/find-edge g from to)))

(defn add-edge [g from to attrs]
  (graph/add-edges g [^:edge from to attrs]))

(defn remove-edge [g from to]
  (if-let [edge (uber/find-edge g from to)]
    (uber/remove-edges g edge) g))

(defn remove-node [g node]
  (uber/remove-nodes g node))

(defn gf-parent-operations [g list-node]
  (filter #(= :operation (:type (uber/attrs g %)))
          (gut/get-predecessors g list-node)))

(defn add-edges-with-new-source [g source-id edges]
  (reduce (fn [g edge] (add-edge g source-id
                                 (uber/dest edge)
                                 (uber/attrs g edge)))
          g edges))

(defn clone-node [g id attrs edges]
  (-> g
      (add-node id attrs)
      (add-edges-with-new-source id edges)))

(defn subset-of-edges-from [g source edges-to-destinations]
  (remove #(get edges-to-destinations (uber/dest %))
          (uber/find-edges g {:src source})))

(defn rearange-list-connections [g list-node gf-funct data-leaf]
  (let [new-id (UUID/randomUUID) new-attrs (uber/attrs g gf-funct)]
    (-> g
        ;;add a new node (clone) for gf operation, old one will be removed
        (clone-node new-id new-attrs (subset-of-edges-from g gf-funct #{list-node}))
        ;;connect new operation ode with data (Str)
        (add-edge new-id data-leaf (attrs-from-edge g gf-funct list-node))
        ;;connect list and new operation nodes
        (add-edge list-node new-id (attrs-from-edge g list-node data-leaf))
        ;;drop old connections which went from list directly to Str
        (remove-edge list-node data-leaf))))

(defn construct-list-structure [g list-node gf-funct]
  (let [list-parent (first (gut/get-predecessors g gf-funct))
        g-with-new-list-edges
        (-> g
            (add-edge list-parent list-node (attrs-from-edge g list-parent gf-funct))
            (remove-edge list-parent gf-funct))]
    (-> (reduce                                             ;;go over children of list node and connect with parent
          (fn [g child] (rearange-list-connections g list-node gf-funct child))
          g-with-new-list-edges
          (gut/get-successors g list-node))
        (remove-edge gf-funct list-node)
        (remove-node gf-funct))))

(defn update-list-categories [g list-node]
  (let [in-edge-category (:category (uber/attrs g (gut/get-in-edge g list-node)))]
    (reduce (fn [g node-or-edge-id]
              (if (some? in-edge-category)
                (assoc-in g [:attrs node-or-edge-id :category] in-edge-category)
                (update-in g [:attrs node-or-edge-id] #(dissoc % :category))))
            g
            (cons list-node (map :id (graph/out-edges g list-node))))))

(defn has-str-child? [g node]
  (some #(let [{:keys [category type]} (attrs g %)]
           (true? (or (= "Str" category) (= :quote type) (= :data type))))
        (graph/successors g node)))

(defn restructure-synonym-node [g node]
  (update-list-categories
    (reduce (fn [g parent]
              (construct-list-structure g node parent))
            g
            (gf-parent-operations g node))
    node))

(defn determine-list-category [g node lang]
  (when (pos-int? (graph/out-degree g node))
    (some (fn [dest]
            (when (every? true? (map (fn [node]
                                       (let [src (find-category g node)]
                                         (or (= src dest) (contains? (get path-map lang) [src dest]))))
                                     (graph/successors g node)))
              dest))
          ["CN" "NP" "AP" "Adv" "IAdv" "AdV" "RS" "S"])))

(defn get-module [cat]
  (if (contains? #{"CN" "IAdv" "AdV"} cat)
    "Grammar"
    "Syntax"))

(defn base-list [category]
  (let [module (get-module category)]
    {:type     :operation
     :name     (str (if (= "Syntax" module) "mkList" "Base") category)
     :category (str "List" category)
     :module   module}))

(defn cons-list [category]
  (let [module (get-module category)]
    {:type     :operation
     :name     (str (if (= "Syntax" module) "mkList" "Cons") category)
     :category (str "List" category)
     :module   module}))

(defn conj-list [category]
  (let [module (get-module category)]
    {:type     :operation
     :name     (str (if (= "Syntax" module) "mk" "Conj") category)
     :category category
     :module   module}))

(defn build-gf-list-graph [g node lang]
  (let [category (determine-list-category g node lang)]
    (if (and (< 1 (graph/out-degree g node)) (some? category))
      (loop [[successor & successors] (gut/get-successors g node)
             list-node (UUID/randomUUID)
             g (let [conj-node (UUID/randomUUID)]
                 (-> g
                     (graph/remove-nodes node)
                     (uber/add-nodes-with-attrs* [[^:node node (conj-list category)]
                                                  [^:node conj-node {:type     :operation
                                                                     :name     "and_Conj"
                                                                     :category "Conj"
                                                                     :module   "Syntax"}]
                                                  [^:node list-node (cons-list category)]])
                     (uber/add-directed-edges* (concat
                                                 [[^:edge node conj-node {:role     :arg
                                                                          :index    0
                                                                          :category "Conj"}]
                                                  [^:edge node list-node {:role     :arg
                                                                          :index    1
                                                                          :category (str "List" category)}]]
                                                 (for [in-edge (graph/in-edges g node)]
                                                   [^:edge (graph/src in-edge) node (attrs g in-edge)])))))]
        (if (= 1 (count successors))
          (-> g
              (uber/add-directed-edges* [[^:edge list-node successor {:role     :arg
                                                                      :index    0
                                                                      :category category}]
                                         [^:edge list-node (first successors) {:role     :arg
                                                                               :index    1
                                                                               :category category}]]))
          (let [child-node (UUID/randomUUID)]
            (recur
              successors
              child-node
              (-> g
                  (uber/add-nodes-with-attrs* [[^:node child-node (base-list category)]])
                  (uber/add-directed-edges* [[^:edge list-node successor {:role     :arg
                                                                          :index    0
                                                                          :category category}]
                                             [^:edge list-node child-node {:role     :arg
                                                                           :index    1
                                                                           :category (str "List" category)}]]))))))
      g)))

(defn build-gf-shuffle-graph [g node lang]
  (reduce (fn [g successors]
            (let [list-node (UUID/randomUUID)]
              (-> g
                  (uber/add-nodes-with-attrs [^:node list-node {:type :sequence}])
                  (uber/add-directed-edges* (cons
                                              [^:edge node list-node {:role :item}]
                                              (map-indexed (fn [index successor]
                                                             [^:edge list-node successor {:role :item :index index}])
                                                           successors)))
                  (build-gf-list-graph list-node lang))))
          (-> g
              (graph/remove-edges* (graph/out-edges g node))
              (assoc-in [:attrs node :type] :synonyms))
          (remove empty? (permutations (graph/successors g node)))))

(defn resolve-lists [g {{lang "*Language"} :constants}]
  (reduce (fn [g node]
            (let [{type :type} (attrs g node)]
              (cond-> g
                      (and
                        (= :synonyms type)
                        (has-str-child? g node)) (restructure-synonym-node node)
                      (= :sequence type) (build-gf-list-graph node lang)
                      (= :shuffle type) (build-gf-shuffle-graph node lang))))
          g
          (filter #(let [{type :type} (attrs g %)]
                     (contains? #{:sequence :shuffle :synonyms} type))
                  (alg/post-traverse g))))
