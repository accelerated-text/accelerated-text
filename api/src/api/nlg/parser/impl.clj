(ns api.nlg.parser.impl
  (:require [clojure.zip :as zip]))

(defmulti build-amr (fn [node] (-> node (get :type) (keyword))))

(defmethod build-amr :default [{:keys [id children] :as node}]
  {:concepts  [{:id    id
                :type  :unknown
                :value (dissoc node :id :children)}]
   :relations (map (fn [{child-id :id}]
                     {:from id
                      :to   child-id
                      :role :unknown})
                   children)})

(defmethod build-amr :placeholder [_])

(defmethod build-amr :Document-plan [{:keys [id segments]}]
  {:concepts  [{:id   id
                :type :document-plan}]
   :relations (map (fn [{segment-id :id}]
                     {:from id
                      :to   segment-id
                      :role :segment})
                   segments)})

(defmethod build-amr :Segment [{:keys [id children]}]
  {:concepts  [{:id   id
                :type :segment}]
   :relations (map (fn [{child-id :id}]
                     {:from id
                      :to   child-id
                      :role :instance})
                   children)})

(defmethod build-amr :AMR [{:keys [roles dictionaryItem]}]
  (when (some? dictionaryItem)
    (let [amr (build-amr dictionaryItem)
          parent-id (get-in amr [:concepts 0 :id])]
      (update amr :relations
              #(->> roles
                    (map-indexed (fn [index {[{child-id :id type :type}] :children name :name}]
                                   (when (not= type "placeholder")
                                     {:from       parent-id
                                      :to         child-id
                                      :role       (keyword (str "ARG" index))
                                      :attributes {:name name}})))
                    (remove nil?)
                    (concat %))))))

(defmethod build-amr :Cell [{:keys [id name children]}]
  {:concepts  [{:id    id
                :type  :data
                :value name}]
   :relations (map (fn [{child-id :id}]
                     {:from id
                      :to   child-id
                      :role :modifier})
                   children)})

(defmethod build-amr :Quote [{:keys [id text children]}]
  {:concepts  [{:id    id
                :type  :quote
                :value text}]
   :relations (map (fn [{child-id :id}]
                     {:from id
                      :to   child-id
                      :role :modifier})
                   children)})

(defmethod build-amr :Dictionary-item [{:keys [id itemId name]}]
  {:concepts [{:id         id
               :type       :dictionary-item
               :value      itemId
               :attributes {:name name}}]})


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
      (assoc :id (format "%02d" index))
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


(defn parse [root]
  (loop [zipper (-> root (preprocess) (make-zipper))
         amr {:relations [] :concepts []}]
    (if (zip/end? zipper)
      (-> amr
          (update :relations vec)
          (update :concepts vec))
      (recur
        (zip/next zipper)
        (merge-with concat amr (build-amr (zip/node zipper)))))))
