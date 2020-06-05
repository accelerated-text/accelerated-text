(ns api.nlg.parser
  (:require [acc-text.nlg.gf.operations :as ops]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [clojure.zip :as zip]))

(def constants #{"*Language"})

(defmulti build-semantic-graph (fn [node _] (-> node (get :type) (keyword))))

(defmethod build-semantic-graph :default [{:keys [id type]} _]
  (throw (Exception. (format "Unknown node type for node id %s: %s" id type))))

(defmethod build-semantic-graph :placeholder [{id :id} _]
  #::sg{:concepts [{:id   id
                    :type :placeholder}]})

(defn definition? [node]
  (= "Define-var" (:type node)))

(defmethod build-semantic-graph :Document-plan [{:keys [id segments kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :document-plan
                     :category kind}]
        :relations (->> segments
                        (remove definition?)
                        (map-indexed (fn [index {segment-id :id kind :kind}]
                                       {:from     id
                                        :to       segment-id
                                        :role     :segment
                                        :index    index
                                        :category kind})))})

(defmethod build-semantic-graph :Segment [{:keys [id children kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :segment
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id kind :kind}]
                                  {:from     id
                                   :to       child-id
                                   :role     :instance
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :AMR [{id :id concept-id :conceptId roles :roles kind :kind} _]
  #::sg{:concepts  [(if (contains? ops/operation-map concept-id)
                      (let [{:keys [module name]} (get ops/operation-map concept-id)]
                        {:id       id
                         :type     :operation
                         :name     name
                         :category kind
                         :module   module})
                      {:id       id
                       :type     :amr
                       :name     concept-id
                       :category kind})]
        :relations (map-indexed (fn [index {[{child-id :id}] :children kind :name label :label}]
                                  (cond-> {:from     id
                                           :to       child-id
                                           :role     :arg
                                           :index    index
                                           :category kind}
                                          (not= kind label) (assoc :name label)))
                                roles)})

(defmethod build-semantic-graph :Cell [{:keys [id name]} _]
  #::sg{:concepts  [{:id       id
                     :type     :data
                     :name     name
                     :category "Str"}]
        :relations []})

(defmethod build-semantic-graph :Quote [{:keys [id text]} _]
  #::sg{:concepts  [{:id       id
                     :type     :quote
                     :value    (or text "")
                     :category "Str"}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item [{:keys [id itemId name kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :dictionary-item
                     :name     itemId
                     :label    name
                     :category kind}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item-modifier [{:keys [id itemId name kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :dictionary-item
                     :name     itemId
                     :label    name
                     :category kind}]
        :relations []})

(defmethod build-semantic-graph :Cell-modifier [{:keys [id name]} _]
  #::sg{:concepts  [{:id       id
                     :type     :data
                     :name     name
                     :category "Str"}]
        :relations []})

(defmethod build-semantic-graph :Modifier [{:keys [id child modifiers kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :modifier
                     :category kind}]
        :relations (cons {:from     id
                          :to       (:id child)
                          :role     :child
                          :category (:kind child)}
                         (map-indexed (fn [index {modifier-id :id kind :kind}]
                                        {:from     id
                                         :to       modifier-id
                                         :role     :modifier
                                         :index    index
                                         :category kind})
                                      modifiers))})

(defmethod build-semantic-graph :Sequence [{:keys [id children kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :sequence
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id kind :kind}]
                                  {:from     id
                                   :to       child-id
                                   :role     :item
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :Shuffle [{:keys [id children kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :shuffle
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id kind :kind}]
                                  {:from     id
                                   :to       child-id
                                   :role     :item
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :One-of-synonyms [{:keys [id children kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :synonyms
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id kind :kind}]
                                  {:from     id
                                   :to       child-id
                                   :role     :item
                                   :index    index
                                   :category kind})
                                children)})

(defmethod build-semantic-graph :If-then-else [{:keys [id conditions kind]} _]
  #::sg{:concepts  [{:id       id
                     :type     :condition
                     :category kind}]
        :relations (map-indexed (fn [index {child-id :id kind :kind}]
                                  {:from     id
                                   :to       child-id
                                   :role     :statement
                                   :index    index
                                   :category kind})
                                conditions)})

(defmethod build-semantic-graph :If-condition [{id :id kind :kind condition :condition then-expression :thenExpression} _]
  #::sg{:concepts  [{:id       id
                     :type     :if-statement
                     :category kind}]
        :relations [{:from id
                     :to   (:id condition)
                     :role :predicate}
                    {:from     id
                     :to       (:id then-expression)
                     :role     :then-expression
                     :category (:kind then-expression)}]})

(defmethod build-semantic-graph :Default-condition [{id :id kind :kind then-expression :thenExpresion} _]
  #::sg{:concepts  [{:id       id
                     :type     :else-statement
                     :category kind}]
        :relations [{:from     id
                     :to       (:id then-expression)
                     :role     :then-expression
                     :category (:kind then-expression)}]})

(defmethod build-semantic-graph :Value-comparison [{:keys [id operator value1 value2]} _]
  #::sg{:concepts  [{:id    id
                     :type  :comparator
                     :value operator}]
        :relations [{:from  id
                     :to    (:id value1)
                     :role  :comparable
                     :index 0}
                    {:from  id
                     :to    (:id value2)
                     :role  :comparable
                     :index 1}]})

(defmethod build-semantic-graph :Value-in [{:keys [id operator value list]} _]
  #::sg{:concepts  [{:id    id
                     :type  :comparator
                     :value operator}]
        :relations [{:from  id
                     :to    (:id list)
                     :role  :comparable
                     :index 0}
                    {:from  id
                     :to    (:id value)
                     :role  :comparable
                     :index 1}]})

(defmethod build-semantic-graph :And-or [{:keys [id operator children]} _]
  #::sg{:concepts  [{:id    id
                     :type  :boolean
                     :value operator}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :input
                                   :index index})
                                children)})

(defmethod build-semantic-graph :Not [{id :id {child-id :id} :value} _]
  #::sg{:concepts  [{:id    id
                     :type  :boolean
                     :value "not"}]
        :relations [{:from id
                     :to   child-id
                     :role :input}]})

(defmethod build-semantic-graph :Xor [{:keys [id value1 value2]} _]
  #::sg{:concepts  [{:id    id
                     :type  :boolean
                     :value "xor"}]
        :relations [{:from id
                     :to   (:id value1)
                     :role :input}
                    {:from id
                     :to   (:id value2)
                     :role :input}]})

(defmethod build-semantic-graph :Define-var [{:keys [id kind value]} _]
  #::sg{:concepts  [{:id       id
                     :type     :variable
                     :category kind}]
        :relations [{:from     id
                     :to       (:id value)
                     :role     :definition
                     :category (:kind value)}]})

(defmethod build-semantic-graph :Get-var [{id :id var-id :name kind :kind} {variables :vars variable-names :var-names}]
  (let [var-name (get variable-names var-id)]
    #::sg{:concepts  [(cond-> {:id       id
                               :type     (if (contains? constants var-name) :constant :reference)
                               :category kind}
                              (contains? variable-names var-id) (assoc :name var-name))]
          :relations (map-indexed (fn [index {var-id :id kind :kind}]
                                    {:from     id
                                     :to       var-id
                                     :role     :pointer
                                     :index    index
                                     :category kind})
                                  (get variables var-id))}))

(defn make-node [{type :type :as node} children]
  (case (keyword type)
    :Document-plan (assoc node :segments children)
    :AMR (assoc node :roles (map (fn [role child] (assoc role :children (list child))) (:roles node) children))
    :Dictionary-item-modifier (assoc node :child (first children))
    :Cell-modifier (assoc node :child (first children))
    :Modifier (-> node (dissoc :modifier) (assoc :child (first children) :modifiers (rest children)))
    :If-then-else (assoc node :conditions children)
    :If-condition (assoc node :condition (first children) :thenExpression (second children))
    :Default-condition (assoc node :thenExpression (first children))
    :Value-comparison (assoc node :value1 (first children) :value2 (second children))
    :Value-in (assoc node :list (first children) :value (second children))
    :Not (assoc node :value (first children))
    :Xor (assoc node :value1 (first children) :value2 (second children))
    :Define-var (assoc node :value (first children))
    (assoc node :children children)))

(defn get-children [{type :type :as node}]
  (case (keyword type)
    :Document-plan (:segments node)
    :AMR (mapcat (fn [{children :children}] (cond-> children (empty? children) (conj nil))) (:roles node))
    :Dictionary-item-modifier [(:child node)]
    :Cell-modifier [(:child node)]
    :Modifier (cons (:child node) (or (when (some? (:modifier node)) [(:modifier node)]) (:modifiers node)))
    :If-then-else (:conditions node)
    :If-condition [(:condition node) (:thenExpression node)]
    :Default-condition [(:thenExpression node)]
    :Value-comparison [(:value1 node) (:value2 node)]
    :Value-in [(:list node) (:value node)]
    :Not [(:value node)]
    :Xor [(:value1 node) (:value2 node)]
    :Define-var [(:value node)]
    (:children node)))

(defn branch? [{type :type :as node}]
  (and
    (map? node)
    (case (keyword type)
      :Document-plan (seq (:segments node))
      :AMR (seq (:roles node))
      :Dictionary-item-modifier (some? (:child node))
      :Cell-modifier (some? (:child node))
      :Modifier (or (some? (:child node)) (some? (:modifier node)) (seq (:modifiers node)))
      :If-then-else (seq (:conditions node))
      :If-condition (or (some? (:condition node)) (some? (:thenExpression node)))
      :Default-condition (some? (:thenExpression node))
      :Value-comparison (or (some? (:value1 node)) (some? (:value2 node)))
      :Value-in (or (some? (:list node)) (some? (:value node)))
      :Not (some? (:value node))
      :Xor (or (some? (:value1 node)) (some? (:value2 node)))
      :Define-var (some? (:value node))
      (seq (:children node)))))

(defn make-zipper [root]
  (zip/zipper branch? get-children make-node root))

(declare preprocess-node)

(defn gen-id [node index]
  (-> node
      (assoc :id (keyword (format "%02d" index)))
      (dissoc :srcId)))

(defn nil->placeholder [node]
  (cond-> node (nil? node) (assoc :type "placeholder")))

(defn rearrange-modifiers [node index]
  (loop [zipper (make-zipper node)
         modifiers []]
    (let [{:keys [type child] :as node} (zip/node zipper)]
      (if-not (and (contains? #{"Dictionary-item-modifier" "Cell-modifier"} type) (some? child))
        (if (seq modifiers)
          (-> {:type "Modifier"}
              (make-node (cons node modifiers))
              (preprocess-node index))
          node)
        (recur (zip/next zipper) (conj modifiers (dissoc node :child)))))))

(defn preprocess-node [node index]
  (-> node (nil->placeholder) (rearrange-modifiers index) (gen-id index)))

(defn preprocess [root]
  (loop [zipper (make-zipper root)
         index 1]
    (if (zip/end? zipper)
      (zip/node zipper)
      (-> zipper
          (zip/edit preprocess-node index)
          (zip/next)
          (recur (inc index))))))

(defn add-description [semantic-graph {variable-names :var-names variables :vars}]
  (let [[{description :value :as concept}] (some (fn [[k v]]
                                                   (when (= v "*Description")
                                                     (sg-utils/get-children
                                                       semantic-graph
                                                       (get-in variables [k 0]))))
                                                 variable-names)]
    (cond-> semantic-graph
            (and
              (some? description)
              (empty? (sg-utils/get-children semantic-graph concept)))
            (assoc ::sg/description description))))

(defn- post-zip [loc]
  (loop [loc loc]
    (if (zip/branch? loc)
      (recur (zip/down loc))
      loc)))

(defn- post-next [loc]
  (if-let [child (zip/right loc)]
    (post-zip child)
    (if-let [parent (zip/up loc)]
      parent
      [(zip/node loc) :end])))

(defn select-kind [kinds]
  (cond
    (and
      (= 2 (count kinds))
      (contains? kinds "Str")) (first (set/difference kinds #{"Str"}))
    (seq kinds) kinds
    :else "Str"))

(defn add-category [{:keys [name type kind] :as node} variables]
  (if (some? kind)
    node
    (assoc node :kind (case (keyword type)
                        :Quote "Str"
                        :Cell "Str"
                        :Cell-modifier "Str"
                        :Get-var (let [kinds (set (remove nil? (map :kind (get variables name))))]
                                   (if (= 1 (count kinds))
                                     (first kinds)
                                     (select-kind kinds)))
                        (let [kinds (set (remove nil? (map :kind (remove definition? (get-children node)))))]
                          (if (= 1 (count kinds))
                            (first kinds)
                            (select-kind kinds)))))))

(defn postprocess [semantic-graph metadata]
  (-> semantic-graph
      (add-description metadata)
      (sg-utils/prune-nil-relations)
      (sg-utils/prune-concepts-by-type :placeholder)
      (sg-utils/prune-unrelated-branches)
      (sg-utils/sort-semantic-graph)))

(def merge-with-concat (partial merge-with concat))

(defn document-plan->semantic-graph
  ([root] (document-plan->semantic-graph root {}))
  ([root {variable-names :var-names :as metadata}]
   (loop [loc (-> root (preprocess) (make-zipper) (post-zip))
          graph #::sg{:relations [] :concepts []}
          variables {}]
     (if (or (zip/end? loc) (empty? root))
       (postprocess graph (assoc metadata :vars variables))
       (let [node (-> (zip/node loc) (add-category variables))]
         (recur
           (post-next (zip/replace loc node))
           (merge-with concat graph (build-semantic-graph
                                      node
                                      {:vars      variables
                                       :var-names variable-names}))
           (cond-> variables
                   (definition? node) (merge-with-concat {(:name node) [node]}))))))))

(s/fdef document-plan->semantic-graph
        :args (s/cat :document-plan map? :metadata map?)
        :ret ::sg/graph)
