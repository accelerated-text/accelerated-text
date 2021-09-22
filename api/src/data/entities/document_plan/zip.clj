(ns data.entities.document-plan.zip
  (:require [clojure.zip :as zip]))

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

(defn make-zipper [document-plan-root]
  (zip/zipper branch? get-children make-node document-plan-root))

(defn post-zip [loc]
  (loop [loc loc]
    (if (zip/branch? loc)
      (recur (zip/down loc))
      loc)))

(defn post-next [loc]
  (if-let [child (zip/right loc)]
    (post-zip child)
    (if-let [parent (zip/up loc)]
      parent
      [(zip/node loc) :end])))
