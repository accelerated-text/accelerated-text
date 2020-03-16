(ns api.nlg.parser
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.spec.alpha :as s]
            [clojure.zip :as zip]))

(def constants #{"*Description" "*Language"})

(defmulti build-semantic-graph (fn [node _] (-> node (get :type) (keyword))))

(defmethod build-semantic-graph :default [{:keys [id type]} _]
  (throw (Exception. (format "Unknown node type for node id %s: %s" id type))))

(defmethod build-semantic-graph :placeholder [{id :id} _]
  #::sg{:concepts [{:id   id
                    :type :placeholder}]})

(defmethod build-semantic-graph :Document-plan [{:keys [id segments]} _]
  #::sg{:concepts  [{:id   id
                     :type :document-plan}]
        :relations (->> segments
                        (remove #(= "Define-var" (:type %)))
                        (map-indexed (fn [index {segment-id :id}]
                                       {:from  id
                                        :to    segment-id
                                        :role  :segment
                                        :index index})))})

(defmethod build-semantic-graph :Segment [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :segment}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :instance
                                   :index index})
                                children)})

(defmethod build-semantic-graph :AMR [{:keys [id conceptId roles]} _]
  #::sg{:concepts  [{:id   id
                     :type :amr
                     :name conceptId}]
        :relations (map-indexed (fn [index {[{child-id :id}] :children name :name label :label}]
                                  (cond-> {:from     id
                                           :to       child-id
                                           :role     :arg
                                           :index    index
                                           :category name}
                                          (not= name label) (assoc :name label)))
                                roles)})

(defmethod build-semantic-graph :Cell [{:keys [id name]} _]
  #::sg{:concepts  [{:id   id
                     :type :data
                     :name name}]
        :relations []})

(defmethod build-semantic-graph :Quote [{:keys [id text]} _]
  #::sg{:concepts  [{:id    id
                     :type  :quote
                     :value (or text "")}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item [{:keys [id itemId name]} _]
  #::sg{:concepts  [{:id    id
                     :type  :dictionary-item
                     :name  itemId
                     :label name}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item-modifier [{:keys [id itemId name]} _]
  #::sg{:concepts  [{:id    id
                     :type  :dictionary-item
                     :name  itemId
                     :label name}]
        :relations []})

(defmethod build-semantic-graph :Cell-modifier [{:keys [id name]} _]
  #::sg{:concepts  [{:id   id
                     :type :data
                     :name name}]
        :relations []})

(defmethod build-semantic-graph :Modifier [{:keys [id child modifiers]} _]
  #::sg{:concepts  [{:id   id
                     :type :modifier}]
        :relations (cons {:from id
                          :to   (:id child)
                          :role :child}
                         (map-indexed (fn [index {modifier-id :id}]
                                        {:from  id
                                         :to    modifier-id
                                         :role  :modifier
                                         :index index})
                                      modifiers))})

(defmethod build-semantic-graph :Sequence [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :sequence}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :item
                                   :index index})
                                children)})

(defmethod build-semantic-graph :Shuffle [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :shuffle}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :item
                                   :index index})
                                children)})

(defmethod build-semantic-graph :One-of-synonyms [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :synonyms}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :item
                                   :index index})
                                children)})

(defmethod build-semantic-graph :If-then-else [{:keys [id conditions]} _]
  #::sg{:concepts  [{:id   id
                     :type :condition}]
        :relations (map-indexed (fn [index {child-id :id}]
                                  {:from  id
                                   :to    child-id
                                   :role  :statement
                                   :index index})
                                conditions)})

(defmethod build-semantic-graph :If-condition [{id :id {predicate-id :id} :condition {expression-id :id} :thenExpression} _]
  #::sg{:concepts  [{:id   id
                     :type :if-statement}]
        :relations [{:from id
                     :to   predicate-id
                     :role :predicate}
                    {:from id
                     :to   expression-id
                     :role :then-expression}]})

(defmethod build-semantic-graph :Default-condition [{id :id {expression-id :id} :thenExpression} _]
  #::sg{:concepts  [{:id   id
                     :type :else-statement}]
        :relations [{:from id
                     :to   expression-id
                     :role :then-expression}]})

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

(defmethod build-semantic-graph :Define-var [{id :id {child-id :id} :value} _]
  #::sg{:concepts  [{:id   id
                     :type :variable}]
        :relations [{:from id
                     :to   child-id
                     :role :definition}]})

(defmethod build-semantic-graph :Get-var [{id :id var-id :name} {variables :vars variable-names :var-names}]
  (let [var-name (get variable-names var-id)]
    #::sg{:concepts  [(cond-> {:id   id
                               :type (if (contains? constants var-name) :constant :reference)}
                              (contains? variable-names var-id) (assoc :name var-name))]
          :relations (map (fn [var-id]
                            {:from id
                             :to   var-id
                             :role :pointer})
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
      (zip/root zipper)
      (-> zipper
          (zip/edit preprocess-node index)
          (zip/next)
          (recur (inc index))))))

(defn postprocess [semantic-graph]
  (-> semantic-graph
      (sg-utils/prune-nil-relations)
      (sg-utils/prune-concepts-by-type :placeholder)
      (sg-utils/prune-unrelated-branches)))

(def merge-with-concat (partial merge-with concat))

(defn document-plan->semantic-graph
  ([root] (document-plan->semantic-graph root {}))
  ([root {variable-names :var-names}]
   (loop [zipper (-> root (preprocess) (make-zipper))
          graph #::sg{:relations [] :concepts []}
          variables {}]
     (if (or (zip/end? zipper) (empty? root))
       (postprocess graph)
       (let [{:keys [id name type] :as node} (zip/node zipper)]
         (recur
           (zip/next zipper)
           (merge-with concat graph (build-semantic-graph
                                      node
                                      {:vars      variables
                                       :var-names variable-names}))
           (cond-> variables
                   (= "Define-var" type) (merge-with-concat {name [id]}))))))))

(s/fdef document-plan->semantic-graph
        :args (s/cat :document-plan map? :metadata map?)
        :ret ::sg/graph)
