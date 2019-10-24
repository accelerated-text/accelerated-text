(ns api.nlg.parser.impl
  (:require [api.utils :as utils]
            [clojure.zip :as zip]))

(defmulti build-amr (fn [node] (-> node (get :type) (keyword))))

(defmethod build-amr :default [{:keys [id children] :as node}]
  {:concepts  [{:id    id
                :type  :unk
                :value (dissoc node :id :children)}]
   :relations (mapv (fn [{child-id :id}]
                      {:from id
                       :to   child-id
                       :role :unk})
                    children)})

(defmethod build-amr :placeholder [_]
  {:concepts  []
   :relations []})

(defmethod build-amr :Document-plan [{:keys [id segments]}]
  {:concepts  [{:id   id
                :type :root}]
   :relations (mapv (fn [{segment-id :id}]
                      {:from id
                       :to   segment-id
                       :role :segment})
                    segments)})

(defmethod build-amr :Segment [{:keys [id children]}]
  {:concepts  [{:id   id
                :type :segment}]
   :relations (mapv (fn [{child-id :id}]
                      {:from id
                       :to   child-id
                       :role :instance})
                    children)})

(defmethod build-amr :AMR [{:keys [id conceptId roles dictionaryItem]}]
  (let [dictionary-item-concept (when (some? dictionaryItem)
                                  (first (:concepts (build-amr dictionaryItem))))]
    {:concepts  (cond-> [{:id   id
                          :type :amr
                          :name conceptId}]
                        (some? dictionary-item-concept) (conj dictionary-item-concept))
     :relations (->> roles
                     (map-indexed (fn [index {[{child-id :id type :type}] :children name :name}]
                                    (when (not= type "placeholder")
                                      {:from       id
                                       :to         child-id
                                       :role       (keyword (str "ARG" (inc index)))
                                       :attributes {:name name}})))
                     (cons
                       (when (some? dictionary-item-concept)
                         {:from id
                          :to   (:id dictionary-item-concept)
                          :role :ARG0}))
                     (remove nil?)
                     (vec))}))

(defmethod build-amr :Relationship [{:keys [id relationshipType children]}]
  {:concepts  [{:id   id
                :type :relationship
                :kind (keyword relationshipType)}]
   :relations (mapv (fn [{child-id :id}]
                      {:from id
                       :to   child-id
                       :role :relationship})
                    children)})

(defmethod build-amr :Cell [{:keys [id name children]}]
  {:concepts  [{:id    id
                :type  :data
                :value name}]
   :relations (mapv (fn [{child-id :itemId}]
                      {:from id
                       :to   child-id
                       :role :modifier})
                    children)})

(defmethod build-amr :Quote [{:keys [id text children]}]
  {:concepts  [{:id    id
                :type  :quote
                :value text}]
   :relations (mapv (fn [{child-id :itemId}]
                      {:from id
                       :to   child-id
                       :role :modifier})
                    children)})

(defmethod build-amr :Dictionary-item [{:keys [itemId name]}]
  {:concepts  [{:id    itemId
                :type  :dictionary-item
                :value name}]
   :relations []})


(defn make-node [{type :type :as node} children]
  (case (keyword type)
    :Document-plan (assoc node :segments (vec children))
    :AMR (assoc node :roles (mapv (fn [role child]
                                    (assoc role :children [child]))
                                  (:roles node) children))
    :Dictionary-item-modifier (assoc node :child (first children))
    (assoc node :children (vec children))))

(defn get-children [{type :type :as node}]
  (case (keyword type)
    :Document-plan (:segments node)
    :AMR (mapcat :children (:roles node))
    :Dictionary-item-modifier (some-> node :child vector)
    (:children node)))

(defn make-zipper [root]
  (zip/zipper map? get-children make-node root))


(declare preprocess-node)

(defn gen-id [node]
  (-> node
      (assoc :id (subs (utils/gen-uuid) 0 8))
      (dissoc :srcId)))

(defn nil->placeholder [node]
  (cond-> node (nil? node) (assoc :type "placeholder")))

(defn preprocess-dict-item [node]
  (cond-> node (contains? node :dictionaryItem) (update :dictionaryItem preprocess-node)))

(defn rearrange-modifiers [node]
  (loop [zipper (make-zipper node)
         modifiers []]
    (let [{:keys [type child] :as node} (zip/node zipper)]
      (if-not (and (= "Dictionary-item-modifier" type) (some? child))
        (cond-> node (seq modifiers) (-> (make-node (concat (get-children node) modifiers))
                                         (preprocess-node)))
        (recur (zip/next zipper) (conj modifiers (-> node
                                                     (dissoc :child)
                                                     (assoc :type :Dictionary-item))))))))

(defn preprocess-node [node]
  (-> node (nil->placeholder) (gen-id) (preprocess-dict-item) (rearrange-modifiers)))

(defn preprocess [root]
  (loop [zipper (make-zipper root)]
    (if (zip/end? zipper)
      (zip/root zipper)
      (-> zipper
          (zip/edit preprocess-node)
          (zip/next)
          (recur)))))


(defn parse [root]
  (loop [zipper (-> root (preprocess) (make-zipper))
         amr {:relations [] :concepts []}]
    (if (zip/end? zipper)
      (-> amr
          (update :relations set)
          (update :concepts set))
      (recur
        (zip/next zipper)
        (merge-with concat amr (build-amr (zip/node zipper)))))))
