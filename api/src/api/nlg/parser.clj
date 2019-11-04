(ns api.nlg.parser
  (:require [acc-text.nlg.spec.semantic-graph :as sg]
            [clojure.spec.alpha :as s]
            [clojure.zip :as zip]))

(defmulti build-semantic-graph (fn [node] (-> node (get :type) (keyword))))

(defmethod build-semantic-graph :default [{:keys [id children] :as node}]
  #::sg{:concepts  [#::sg{:id    id
                          :type  :unknown
                          :value (dissoc node :id :children)}]
        :relations (map (fn [{child-id :id}]
                          #::sg{:from id
                                :to   child-id
                                :role :unknown})
                        children)})

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
  #::sg{:concepts  (if (some? dictionaryItem)
                     (-> dictionaryItem (build-semantic-graph) (get ::sg/concepts))
                     [#::sg{:id    id
                            :type  :amr
                            :value conceptId}])
        :relations (->> roles
                        (map-indexed (fn [index {[{child-id :id type :type}] :children name :name}]
                                       (when (not= type "placeholder")
                                         #::sg{:from       id
                                               :to         child-id
                                               :role       (keyword (str "ARG" index))
                                               :attributes #::sg{:name name}})))
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

(defmethod build-semantic-graph :Dictionary-item [{:keys [id itemId name]}]
  #::sg{:concepts  [#::sg{:id         id
                          :type       :dictionary-item
                          :value      itemId
                          :attributes #::sg{:name name}}]
        :relations []})


(defn make-node [{type :type :as node} children]
  (case (keyword type)
    :Document-plan (assoc node :segments children)
    :AMR (assoc node :roles (map (fn [role child]
                                   (assoc role :children (list child)))
                                 (:roles node) children))
    :Dictionary-item-modifier (assoc node :child (first children))
    (assoc node :children children)))

(defn get-children [{type :type :as node}]
  (case (keyword type)
    :Document-plan (:segments node)
    :AMR (mapcat :children (:roles node))
    :Dictionary-item-modifier (some-> node :child vector)
    (:children node)))

(defn make-zipper [root]
  (zip/zipper map? get-children make-node root))

(declare preprocess-node)

(defn gen-id [node index]
  (-> node
      (assoc :id (keyword (format "%02d" index)))
      (dissoc :srcId)))

(defn nil->placeholder [node]
  (cond-> node (nil? node) (assoc :type "placeholder")))

(defn preprocess-dict-item [node index]
  (cond-> node (contains? node :dictionaryItem) (update :dictionaryItem #(preprocess-node % index))))

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
  (-> node (nil->placeholder) (preprocess-dict-item index) (rearrange-modifiers index) (gen-id index)))

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
    (if (zip/end? zipper)
      amr
      (recur
        (zip/next zipper)
        (merge-with concat amr (build-semantic-graph (zip/node zipper)))))))

(s/fdef document-plan->semantic-graph
        :args (s/cat :document-plan map?)
        :ret ::sg/graph)
