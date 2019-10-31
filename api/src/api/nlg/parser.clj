(ns api.nlg.parser
  (:require [acc-text.nlg.spec.document-plan :as dp]
            [clojure.spec.alpha :as s]
            [clojure.zip :as zip]))

(defmulti build-document-plan (fn [node] (-> node (get :type) (keyword))))

(defmethod build-document-plan :default [{:keys [id children] :as node}]
  #::dp{:concepts  [#::dp{:id    id
                          :type  :unknown
                          :value (dissoc node :id :children)}]
        :relations (map (fn [{child-id :id}]
                          #::dp{:from id
                                :to   child-id
                                :role :unknown})
                        children)})

(defmethod build-document-plan :placeholder [_]
  #::dp{:concepts  []
        :relations []})

(defmethod build-document-plan :Document-plan [{:keys [id segments]}]
  #::dp{:concepts  [#::dp{:id   id
                          :type :document-plan}]
        :relations (map (fn [{segment-id :id}]
                          #::dp{:from id
                                :to   segment-id
                                :role :segment})
                        segments)})

(defmethod build-document-plan :Segment [{:keys [id children]}]
  #::dp{:concepts  [#::dp{:id   id
                          :type :segment}]
        :relations (map (fn [{child-id :id}]
                          #::dp{:from id
                                :to   child-id
                                :role :instance})
                        children)})

(defmethod build-document-plan :AMR [{:keys [id conceptId roles dictionaryItem]}]
  #::dp{:concepts  (if (some? dictionaryItem)
                     (-> dictionaryItem (build-document-plan) (get ::dp/concepts))
                     [#::dp{:id    id
                            :type  :amr
                            :value conceptId}])
        :relations (->> roles
                        (map-indexed (fn [index {[{child-id :id type :type}] :children name :name}]
                                       (when (not= type "placeholder")
                                         #::dp{:from       id
                                               :to         child-id
                                               :role       (keyword (str "ARG" index))
                                               :attributes #::dp{:name name}})))
                        (remove nil?))})

(defmethod build-document-plan :Cell [{:keys [id name children]}]
  #::dp{:concepts  [#::dp{:id    id
                          :type  :data
                          :value name}]
        :relations (map (fn [{child-id :id}]
                          #::dp{:from id
                                :to   child-id
                                :role :modifier})
                        children)})

(defmethod build-document-plan :Quote [{:keys [id text children]}]
  #::dp{:concepts  [#::dp{:id    id
                          :type  :quote
                          :value text}]
        :relations (map (fn [{child-id :id}]
                          #::dp{:from id
                                :to   child-id
                                :role :modifier})
                        children)})

(defmethod build-document-plan :Dictionary-item [{:keys [id itemId name]}]
  #::dp{:concepts  [#::dp{:id         id
                          :type       :dictionary-item
                          :value      itemId
                          :attributes #::dp{:name name}}]
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

(defn parse-document-plan [root]
  (loop [zipper (-> root (preprocess) (make-zipper))
         amr #::dp{:relations [] :concepts []}]
    (if (zip/end? zipper)
      amr
      (recur
        (zip/next zipper)
        (merge-with concat amr (build-document-plan (zip/node zipper)))))))

(s/fdef parse-document-plan
        :args (s/cat :document-plan map?)
        :ret ::dp/graph)
