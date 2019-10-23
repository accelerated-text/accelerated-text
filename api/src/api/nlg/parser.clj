(ns api.nlg.parser
  (:require [amr-spec]
            [api.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.zip :as zip]))

(defmulti build-amr (fn [node] (-> node (get :type) (keyword))))

(defmethod build-amr :default [{:keys [id children] :as node}]
  {:concepts  [{:id    id
                :type  :unk
                :value (dissoc node :id :children)}]
   :relations (mapv (fn [{child-id :id}]
                      {:from id
                       :to   child-id
                       :type :unk})
                    children)})

(defmethod build-amr :Document-plan [{:keys [id segments]}]
  {:concepts  [{:id   id
                :type :root}]
   :relations (mapv (fn [{segment-id :id}]
                      {:from id
                       :to   segment-id
                       :type :segment})
                    segments)})

(defmethod build-amr :Segment [{:keys [id children textType]}]
  {:concepts  [{:id   id
                :type :segment
                :kind (keyword textType)}]
   :relations (mapv (fn [{child-id :id}]
                      {:from id
                       :to   child-id
                       :type :instance})
                    children)})

(defmethod build-amr :AMR [{:keys [id name conceptId roles dictionaryItem]}]
  {:concepts  [{:id   id
                :type (keyword conceptId)
                :name name}
               {:id   (:itemId dictionaryItem)
                :type :dictionary-item
                :name (:name dictionaryItem)}]
   :relations (vec
                (cons
                  {:from id
                   :to   (:itemId dictionaryItem)
                   :type :ARG0}
                  (map-indexed (fn [index role]
                                 {:from id
                                  :to   (:id role)
                                  :type (keyword (str "ARG" (inc index)))})
                               roles)))})

(defmethod build-amr :Relationship [{:keys [id relationshipType children]}]
  {:concepts  [{:id   id
                :type :relationship
                :kind (keyword relationshipType)}]
   :relations (mapv (fn [{child-id :id}]
                      {:from id
                       :to   child-id
                       :type :relationship})
                    children)})

(defmethod build-amr :Cell [{:keys [id name]}]
  {:concepts  [{:id   id
                :type :data
                :name name}]
   :relations []})

(defmethod build-amr :Quote [{:keys [id text]}]
  {:concepts  [{:id    id
                :type  :quote
                :value text}]
   :relations []})

(defmethod build-amr :Dictionary-item-modifier [{:keys [id name child]}]
  {:concepts  [{:id   id
                :type :modifier
                :name name}]
   :relations [{:from id
                :to   (:id child)
                :type :ARG0-of}]})

(defn make-zipper [root]
  (zip/zipper
    map?
    (fn [node]
      (cond
        (:segments node) (:segments node)
        (:roles node) (:roles node)
        (:children node) (:children node)
        (:child node) (-> node :child vector)))
    (fn [{type :type :as node} children]
      (case (keyword type)
        :Document-plan (assoc node :segments (vec children))
        :AMR (assoc node :roles (vec children))
        :Dictionary-item-modifier (assoc node :child (first children))
        (assoc node :children (vec children))))
    root))

(defn preprocess [root]
  (loop [zipper (make-zipper root)]
    (if (zip/end? zipper)
      (zip/root zipper)
      (let [id (subs (utils/gen-uuid) 0 8)]
        (recur (-> zipper
                   (zip/edit #(assoc % :id id))
                   (zip/next)))))))

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

(s/fdef parse
        :args (s/cat :document-plan any?)
        :ret :amr-spec/amr)
