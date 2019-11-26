(ns api.nlg.parser
  (:require [acc-text.nlg.semantic-graph :as sg]
            [clojure.spec.alpha :as s]
            [clojure.zip :as zip]))

(defmulti build-semantic-graph (fn [node] (-> node (get :type) (keyword))))

(defmethod build-semantic-graph :default [{:keys [id type]}]
  (throw (Exception. (format "Unknown node type for node id %s: %s" id type))))

(defmethod build-semantic-graph :placeholder [_]
  #::sg{:concepts  []
        :relations []})

(defmethod build-semantic-graph :Document-plan [{:keys [id segments]}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :document-plan}]
        :relations (map (fn [{segment-id :id}]
                          #::sg{:from id
                                :to   segment-id
                                :role :segment})
                        segments)})

(defmethod build-semantic-graph :Segment [{:keys [id children]}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :segment}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :instance})
                        children)})

(defmethod build-semantic-graph :AMR [{:keys [id conceptId roles dictionaryItem]}]
  #::sg{:concepts  [#::sg{:id    id
                          :type  :amr
                          :value conceptId}]
        :relations (->> roles
                        (map-indexed (fn [index {[{child-id :id type :type}] :children name :name}]
                                       (when (not= type "placeholder")
                                         #::sg{:from       id
                                               :to         child-id
                                               :role       (keyword (str "ARG" index))
                                               :attributes #::sg{:name name}})))
                        (cons (when (and (some? dictionaryItem) (not= (:type dictionaryItem) "placeholder"))
                                #::sg{:from id
                                      :to   (:id dictionaryItem)
                                      :role :function}))
                        (remove nil?))})

(defmethod build-semantic-graph :Cell [{:keys [id name children]}]
  #::sg{:concepts  [#::sg{:id    id
                          :type  :data
                          :value name}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :modifier})
                        children)})

(defmethod build-semantic-graph :Quote [{:keys [id text children]}]
  #::sg{:concepts  [#::sg{:id    id
                          :type  :quote
                          :value text}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :modifier})
                        children)})

(defmethod build-semantic-graph :Dictionary-item [{:keys [id itemId name children]}]
  #::sg{:concepts  [#::sg{:id         id
                          :type       :dictionary-item
                          :value      itemId
                          :attributes #::sg{:name name}}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :modifier})
                        children)})

(defmethod build-semantic-graph :Dictionary-item-modifier [node]
  (-> node
      (assoc :type "Dictionary-item")
      (build-semantic-graph)))

(defmethod build-semantic-graph :Sequence [{:keys [id children]}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :sequence}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :item})
                        children)})

(defmethod build-semantic-graph :Shuffle [{:keys [id children]}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :shuffle}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :item})
                        children)})

(defmethod build-semantic-graph :One-of-synonyms [{:keys [id children]}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :synonyms}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :synonym})
                        children)})

(defmethod build-semantic-graph :If-then-else [{:keys [id conditions]}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :condition}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :statement})
                        conditions)})

(defmethod build-semantic-graph :If-condition [{id :id {predicate-id :id} :condition {expression-id :id} :thenExpression}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :if-statement}]
        :relations [#::sg{:from id
                          :to   predicate-id
                          :role :predicate}
                    #::sg{:from id
                          :to   expression-id
                          :role :expression}]})

(defmethod build-semantic-graph :Default-condition [{id :id {expression-id :id} :thenExpression}]
  #::sg{:concepts  [#::sg{:id   id
                          :type :default-statement}]
        :relations [#::sg{:from id
                          :to   expression-id
                          :role :expression}]})

(defmethod build-semantic-graph :Value-comparison [{:keys [id operator value1 value2]}]
  #::sg{:concepts  [#::sg{:id    id
                          :value operator
                          :type  :comparator}]
        :relations [#::sg{:from id
                          :to   (:id value1)
                          :role :comparable}
                    #::sg{:from id
                          :to   (:id value2)
                          :role :comparable}]})

(defn make-node [{type :type :as node} children]
  (case (keyword type)
    :Document-plan (assoc node :segments children)
    :AMR (assoc node :dictionaryItem (first children)
                     :roles (map (fn [role child]
                                   (assoc role :children (list child)))
                                 (:roles node) (rest children)))
    :Dictionary-item-modifier (assoc node :child (first children))
    :If-then-else (assoc node :conditions children)
    :If-condition (assoc node :condition (first children) :thenExpression (second children))
    :Default-condition (assoc node :thenExpression (first children))
    :Value-comparison (assoc node :value1 (first children) :value2 (second children))
    (assoc node :children children)))

(defn get-children [{type :type :as node}]
  (case (keyword type)
    :Document-plan (:segments node)
    :AMR (cons (:dictionaryItem node) (mapcat :children (:roles node)))
    :Dictionary-item-modifier [(:child node)]
    :If-then-else (:conditions node)
    :If-condition [(:condition node) (:thenExpression node)]
    :Default-condition [(:thenExpression node)]
    :Value-comparison [(:value1 node) (:value2 node)]
    (:children node)))

(defn branch? [{type :type :as node}]
  (and
    (map? node)
    (case (keyword type)
      :Document-plan (seq (:segments node))
      :AMR (or (some? (:dictionaryItem node)) (seq (:roles node)))
      :Dictionary-item-modifier (some? (:child node))
      :If-then-else (seq (:conditions node))
      :If-condition (some some? [(:condition node) (:thenExpression node)])
      :Default-condition (some? (:thenExpression node))
      :Value-comparison (some some? [(:value1 node) (:value2 node)])
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
      (if-not (and (= "Dictionary-item-modifier" type) (some? child))
        (cond-> node (seq modifiers) (-> (make-node (concat (get-children node) modifiers))
                                         (preprocess-node index)))
        (recur (zip/next zipper) (conj modifiers (-> node
                                                     (dissoc :child)
                                                     (assoc :type "Dictionary-item"))))))))

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

(defn document-plan->semantic-graph [root]
  (loop [zipper (-> root (preprocess) (make-zipper))
         amr #::sg{:relations [] :concepts []}]
    (if (or (zip/end? zipper) (empty? root))
      amr
      (recur
        (zip/next zipper)
        (merge-with concat amr (build-semantic-graph (zip/node zipper)))))))

(s/fdef document-plan->semantic-graph
        :args (s/cat :document-plan map?)
        :ret ::sg/graph)
