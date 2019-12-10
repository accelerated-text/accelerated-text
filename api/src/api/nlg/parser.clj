(ns api.nlg.parser
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [clojure.spec.alpha :as s]
            [clojure.zip :as zip]))

(defmulti build-semantic-graph (fn [node _] (-> node (get :type) (keyword))))

(defmethod build-semantic-graph :default [{:keys [id type]} _]
  (throw (Exception. (format "Unknown node type for node id %s: %s" id type))))

(defmethod build-semantic-graph :placeholder [{id :id} _]
  #::sg{:concepts  [{:id   id
                     :type :placeholder}]
        :relations []})

(defmethod build-semantic-graph :Document-plan [{:keys [id segments]} _]
  #::sg{:concepts  [{:id   id
                     :type :document-plan}]
        :relations (->> segments
                        (remove #(= "Define-var" (:type %)))
                        (map (fn [{segment-id :id}]
                               {:from id
                                :to   segment-id
                                :role :segment})))})

(defmethod build-semantic-graph :Segment [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :segment}]
        :relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :instance})
                        children)})

(defmethod build-semantic-graph :AMR [{:keys [id conceptId roles dictionaryItem]} _]
  #::sg{:concepts  [{:id    id
                     :type  :amr
                     :value conceptId}]
        :relations (cons {:from id
                          :to   (:id dictionaryItem)
                          :role :function}
                         (map-indexed (fn [index {[{child-id :id}] :children name :name}]
                                        {:from       id
                                         :to         child-id
                                         :role       (keyword (str "ARG" index))
                                         :attributes {:name name}})
                                      roles))})

(defmethod build-semantic-graph :Cell [{:keys [id name]} _]
  #::sg{:concepts  [{:id    id
                     :type  :data
                     :value name}]
        :relations []})

(defmethod build-semantic-graph :Quote [{:keys [id text]} _]
  #::sg{:concepts  [{:id    id
                     :type  :quote
                     :value text}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item [{:keys [id itemId name]} _]
  #::sg{:concepts  [{:id         id
                     :type       :dictionary-item
                     :value      itemId
                     :attributes {:name name}}]
        :relations []})

(defmethod build-semantic-graph :Dictionary-item-modifier [{:keys [id itemId name]} _]
  #::sg{:concepts  [{:id         id
                     :type       :dictionary-item
                     :value      itemId
                     :attributes {:name name}}]
        :relations []})

(defmethod build-semantic-graph :Cell-modifier [{:keys [id name]} _]
  #::sg{:concepts  [{:id    id
                     :type  :data
                     :value name}]
        :relations []})

(defmethod build-semantic-graph :Modifier [{:keys [id child modifiers]} _]
  #::sg{:concepts  [{:id   id
                     :type :modifier}]
        :relations (cons {:from id
                          :to   (:id child)
                          :role :child}
                         (map (fn [{modifier-id :id}]
                                {:from id
                                 :to   modifier-id
                                 :role :modifier})
                              modifiers))})

(defmethod build-semantic-graph :Sequence [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :sequence}]
        :relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :item})
                        children)})

(defmethod build-semantic-graph :Shuffle [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :shuffle}]
        :relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :item})
                        children)})

(defmethod build-semantic-graph :One-of-synonyms [{:keys [id children]} _]
  #::sg{:concepts  [{:id   id
                     :type :synonyms}]
        :relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :synonym})
                        children)})

(defmethod build-semantic-graph :If-then-else [{:keys [id conditions]} _]
  #::sg{:concepts  [{:id   id
                     :type :condition}]
        :relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :statement})
                        conditions)})

(defmethod build-semantic-graph :If-condition [{id :id {predicate-id :id} :condition {expression-id :id} :thenExpression} _]
  #::sg{:concepts  [{:id   id
                     :type :if-statement}]
        :relations [{:from id
                     :to   predicate-id
                     :role :predicate}
                    {:from id
                     :to   expression-id
                     :role :expression}]})

(defmethod build-semantic-graph :Default-condition [{id :id {expression-id :id} :thenExpression} _]
  #::sg{:concepts  [{:id   id
                     :type :default-statement}]
        :relations [{:from id
                     :to   expression-id
                     :role :expression}]})

(defmethod build-semantic-graph :Value-comparison [{:keys [id operator value1 value2]} _]
  #::sg{:concepts  [{:id    id
                     :value operator
                     :type  :comparator}]
        :relations [{:from id
                     :to   (:id value1)
                     :role :comparable}
                    {:from id
                     :to   (:id value2)
                     :role :comparable}]})

(defmethod build-semantic-graph :Value-in [{:keys [id operator value list]} _]
  #::sg{:concepts  [{:id    id
                     :value operator
                     :type  :comparator}]
        :relations [{:from id
                     :to   (:id list)
                     :role :entity}
                    {:from id
                     :to   (:id value)
                     :role :comparable}]})

(defmethod build-semantic-graph :And-or [{:keys [id operator children]} _]
  #::sg{:concepts  [{:id    id
                     :value operator
                     :type  :boolean}]
        :relations (map (fn [{child-id :id}]
                          {:from id
                           :to   child-id
                           :role :input})
                        children)})

(defmethod build-semantic-graph :Not [{id :id {child-id :id} :value} _]
  #::sg{:concepts  [{:id    id
                     :value "not"
                     :type  :boolean}]
        :relations [{:from id
                     :to   child-id
                     :role :input}]})

(defmethod build-semantic-graph :Xor [{:keys [id value1 value2]} _]
  #::sg{:concepts  [{:id    id
                     :value "xor"
                     :type  :boolean}]
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

(defmethod build-semantic-graph :Get-var [{:keys [id name]} variables]
  #::sg{:concepts  [{:id   id
                     :type :reference}]
        :relations (map (fn [variable-id]
                          {:from id
                           :to   variable-id
                           :role :pointer})
                        (get variables name))})

(defn make-node [{type :type :as node} children]
  (case (keyword type)
    :Document-plan (assoc node :segments children)
    :AMR (assoc node :dictionaryItem (first children)
                     :roles (map (fn [role child]
                                   (assoc role :children (list child)))
                                 (:roles node) (rest children)))
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
    :AMR (cons (:dictionaryItem node) (mapcat :children (:roles node)))
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
      :AMR (or (some? (:dictionaryItem node)) (seq (:roles node)))
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

(defn document-plan->semantic-graph [root]
  (loop [zipper (-> root (preprocess) (make-zipper))
         graph #::sg{:relations [] :concepts []}
         variables {}]
    (if (or (zip/end? zipper) (empty? root))
      (postprocess graph)
      (let [{:keys [id name type] :as node} (zip/node zipper)]
        (recur
          (zip/next zipper)
          (merge-with concat graph (build-semantic-graph node variables))
          (cond-> variables
                  (= "Define-var" type) ((partial merge-with concat) {name [id]})))))))

(s/fdef document-plan->semantic-graph
        :args (s/cat :document-plan map?)
        :ret ::sg/graph)
