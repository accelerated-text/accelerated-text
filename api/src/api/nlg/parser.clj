(ns api.nlg.parser
  (:require [amr-spec]
            [api.nlg.dictionary :as dictionary-api]
            [api.utils :as utils]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.zip :as zip]
            [clojure.set :as set]))

(defmulti build-concept (fn [node] (-> node (get :type) (keyword))))

(defmethod build-concept :default [node]
  {:id    (utils/gen-uuid)
   :type  :unk
   :value (dissoc node :segments :roles :children :child)})

(defmethod build-concept :Cell [{name :name}]
  {:id   (utils/gen-uuid)
   :type :cell
   :name name})

(defmethod build-concept :quote [{text :text}]
  {:id    (utils/gen-uuid)
   :type  :quote
   :value text})

(defmethod build-concept :dictionaryItem [{name :text}]
  {:id    (utils/gen-uuid)
   :type  :dictionary-item
   :name  name
   :value (dictionary-api/search (string/lower-case name) :default)})

(defmethod build-concept :dictionaryItemModifier [{name :name}]
  {:id   (utils/gen-uuid)
   :type :modifier
   :name name})

(defmethod build-concept :Document-plan [_]
  {:id   (utils/gen-uuid)
   :type :root})

(defmethod build-concept :Segment [_]
  {:id   (utils/gen-uuid)
   :type :segment})

(defn relate [parent-concept child-concepts]
  (map-indexed (fn [index concept]
                 {:from (or (:id parent-concept) :ROOT)
                  :to   (:id concept)
                  :type (keyword (str "ARG" index))})
               child-concepts))

(defn known? [concept]
  (not= :unk (:type concept)))

(defn has-children? [node]
  (some? (seq (keys (select-keys node [:segments :roles :children :child])))))

(defn get-children [node]
  (cond
    (:segments node) (:segments node)
    (:roles node) (:roles node)
    (:children node) (:children node)
    (:child node) (-> node :child vector)))

(defn parse [root]
  (loop [zipper (zip/zipper #(and (map? %) (has-children? %)) get-children (fn [& _]) root)
         amr {:relations #{} :concepts #{}}]
    (if (zip/end? zipper)
      amr
      (let [node (zip/node zipper)
            parent-concept (build-concept node)
            child-concepts (map build-concept (get-children node))]
        (recur
          (zip/next zipper)
          (-> amr
              (update :concepts #(set/union % (into #{} (conj child-concepts parent-concept))))
              (update :relations #(set/union % (into #{} (relate parent-concept child-concepts))))))))))

(s/fdef parse
        :args (s/cat :document-plan any?)
        :ret :amr-spec/amr)
