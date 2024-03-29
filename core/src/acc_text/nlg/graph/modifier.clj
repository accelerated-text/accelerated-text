(ns acc-text.nlg.graph.modifier
  (:require [acc-text.nlg.gf.modifiers :refer [modifier-map]]
            [acc-text.nlg.gf.paths :refer [path-map]]
            [acc-text.nlg.graph.utils :refer [get-successors]]
            [acc-text.nlg.semantic-graph :as sg]
            [clojure.math.combinatorics :refer [cartesian-product]]
            [loom.alg :as alg]
            [loom.attr :refer [attrs]]
            [loom.graph :as graph]
            [ubergraph.core :as uber])
  (:import (java.util UUID)))

(defn validate-cats [lang cats synced-cats]
  (let [[modifier-cat child-cat] cats]
    (if (contains? (get modifier-map lang) synced-cats)
      synced-cats
      (throw
       (Exception.
        ^String
        (cond
          (nil? child-cat) "Unable to determine child category"
          (nil? modifier-cat) "Unable to determine modifier category"
          :else (format "Unable to glue %s with %s" child-cat modifier-cat)))))))

(defn find-modified-node [g modifier-node]
  (some #(when (= :child (:role (attrs g %))) (graph/dest %))
        (graph/out-edges g modifier-node)))

(defn find-modifiers [g modifier-node]
  (->> (graph/out-edges g modifier-node)
       (filter #(= :modifier (:role (attrs g %))))
       (sort-by #(:index (attrs g %)))
       (map graph/dest)
       (reverse)))

(defn find-category [g node]
  (->> node
       (iterate #(let [[fist-child second-child] (get-successors g %)]
                   (or (when (and
                              (= :modifier (:type (attrs g node)))
                              (not= "Conj" (:category (attrs g second-child))))
                         second-child)
                       fist-child)))
       (take-while some?)
       (some #(let [{:keys [category type]} (attrs g %)]
                (if (contains? #{:quote :data} type)
                  "Str"
                  category)))))

(defn find-path-to-utt [lang cat]
  (->> (get-in path-map [lang [cat "Utt"]])
       (::sg/concepts)
       (map :category)
       (reverse)
       (cons cat)))

(defn sync-categories [lang [modifier-cat child-cat]]
  (->> (cartesian-product (find-path-to-utt lang modifier-cat) (find-path-to-utt lang child-cat))
       (some #(when (contains? (get modifier-map lang) %) %))
       (validate-cats lang [modifier-cat child-cat])))

(defn determine-categories [lang [modifier-cat child-cat]]
  (sync-categories
   lang
   (cond
     (= "QCl" child-cat) [modifier-cat "QS"]
     (and
      (contains? #{"Cl" "S" "RCl"} child-cat)
      (not (contains? #{"Adv" "A"} modifier-cat))) [modifier-cat "RS"]
     :else (case [modifier-cat child-cat]
             ["Str" "Str"] ["A" "N"]
             ["A" "Str"] ["A" "N"]
             ["A" "A"] ["AP" "VP"]
             ["A" "AP"] ["AP" "VP"]
             ["A" "Adv"] ["AP" "VP"]
             ["A" "Cl"] ["AP" "S"]
             ["N" "Str"] ["CN" "NP"]
             ["N" "A"] ["NP" "A"]
             ["V" "V"] ["VV" "VP"]
             ["V" "VP"] ["VV" "VP"]
             ["V" "N"] ["VV" "VP"]
             ["V" "CN"] ["VV" "VP"]
             ["V" "Str"] ["VV" "VP"]
             ["Adv" "V"] ["Adv" "S"]
             ["Adv" "N"] ["Adv" "S"]
             ["Adv" "Str"] ["Adv" "S"]
             ["Adv" "A"] ["Adv" "S"]
             [modifier-cat child-cat]))))

(defn find-nearest-node [g child category]
  (->> child
       (alg/post-traverse g)
       (map #(uber/node-with-attrs g %))
       (some (fn [[node {cat :category}]]
               (when (= category cat) node)))))

(defn resolve-modifier [g node modifier child lang]
  (let [modifier-attrs (attrs g modifier)
        modifier-cat   (find-category g modifier)
        child-cat      (find-category g child)
        child-node     (find-nearest-node g child modifier-cat)]
    (uber/build-graph
     g
     (cond
       (= "Punct" modifier-cat) (uber/multidigraph
                                 [^:node node {:type :operation, :name "mkText", :category "Text", :module "Syntax"}]
                                 [^:edge node child {:role :arg :index 0 :category "Utt"}]
                                 [^:edge node modifier {:role :arg :index 1}])
       (= "Subj" modifier-cat) (uber/multidigraph
                                [^:node node {:type :operation, :name "mkAdv", :category "Adv", :module "Syntax"}]
                                [^:edge node modifier {:role :arg :index 0 :category "Subj"}]
                                [^:edge node child {:role :arg :index 1 :category "S"}])
       (= "Voc" modifier-cat) (uber/multidigraph
                               [^:node node {:type :operation, :name "mkPhr", :category "Phr", :module "Syntax"}]
                               [^:edge node child {:role :arg :index 1 :category "Utt"}]
                               [^:edge node modifier {:role :arg :index 2 :category "Voc"}])
       (and
        (contains? #{"Cl" "S"} modifier-cat)
        (contains? #{"Cl" "RCl" "RS"} child-cat)) (uber/multidigraph
                                                   [^:node node {:type :operation, :name "RelS", :category "S", :module "Grammar"}]
                                                   [^:edge node modifier {:role :arg :index 0 :category "S"}]
                                                   [^:edge node child {:role :arg :index 1 :category "RS"}])
       (and
        (some? child-node)
        (= "Conj" modifier-cat)) (uber/multidigraph
                                  [^:node node {:type :modifier :category child-cat}]
                                  [^:node child-node (dissoc modifier-attrs :position)]
                                  [^:edge node child {:type :instance :category child-cat}])
       :else (let [[synced-modifier-cat synced-child-cat] (determine-categories lang [modifier-cat child-cat])]
               (uber/multidigraph
                [^:node node (first (get-in modifier-map [lang [synced-modifier-cat synced-child-cat]]))]
                [^:edge node modifier {:role :arg :index 0 :category synced-modifier-cat}]
                [^:edge node child {:role :arg :index 1 :category synced-child-cat}]))))))

(defn resolve-modifiers [g {{lang "*Language"} :constants}]
  (reduce (fn [g modifier-node]
            (loop [[modifier & modifiers] (find-modifiers g modifier-node)
                   child (find-modified-node g modifier-node)
                   g     g]
              (if (some nil? [modifier child])
                (cond-> (graph/remove-nodes g modifier-node)
                  (some? child) (uber/add-directed-edges*
                                 (for [edge (graph/in-edges g modifier-node)]
                                   [^:edge (graph/src edge) child (attrs g edge)]))
                  (some? modifier) (uber/add-directed-edges*
                                    (for [edge (graph/in-edges g modifier-node)]
                                      [^:edge (graph/src edge) modifier (attrs g edge)])))
                (let [node (UUID/randomUUID)]
                  (recur
                   modifiers
                   node
                   (resolve-modifier g node modifier child lang))))))
          g
          (filter #(= :modifier (:type (attrs g %)))
                  (alg/post-traverse g))))
