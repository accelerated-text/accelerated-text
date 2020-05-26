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
  (if (contains? (get modifier-map lang) synced-cats)
    synced-cats
    (let [[modifier-cat child-cat] cats]
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
       (iterate #(first (get-successors g %)))
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

(defn sync-categories [lang modifier-cat child-cat]
  (case [modifier-cat child-cat]
    ["A" "Str"] ["A" "N"]
    ["A" "A"] ["AP" "VP"]
    ["A" "AP"] ["AP" "VP"]
    ["A" "Adv"] ["AP" "VP"]
    ["A" "Cl"] ["AP" "S"]
    ["N" "Str"] ["CN" "NP"]
    ["V" "V"] ["VV" "VP"]
    ["V" "VP"] ["VV" "VP"]
    ["V" "N"] ["VV" "VP"]
    ["V" "CN"] ["VV" "VP"]
    ["V" "Str"] ["VV" "VP"]
    ["Adv" "V"] ["Adv" "S"]
    ["Adv" "N"] ["Adv" "S"]
    ["Adv" "Str"] ["Adv" "S"]
    ["Adv" "A"] ["Adv" "S"]
    (->> (cartesian-product (find-path-to-utt lang modifier-cat) (find-path-to-utt lang child-cat))
         (some #(when (contains? (get modifier-map lang) %) %))
         (validate-cats lang [modifier-cat child-cat]))))

(defn resolve-modifiers [g {{lang "*Language"} :constants}]
  (reduce (fn [g modifier-node]
            (loop [[modifier & modifiers] (find-modifiers g modifier-node)
                   child (find-modified-node g modifier-node)
                   g g]
              (if-not (and (some? child) (some? modifier))
                (-> g
                    (graph/remove-nodes modifier-node)
                    (uber/add-directed-edges*
                      (for [edge (graph/in-edges g modifier-node)]
                        [^:edge (graph/src edge) child (attrs g edge)])))
                (let [root-node (UUID/randomUUID)
                      [modifier-cat child-cat] (sync-categories
                                                 lang
                                                 (find-category g modifier)
                                                 (find-category g child))]
                  (recur
                    modifiers
                    root-node
                    (uber/build-graph
                      g
                      (uber/multidigraph
                        [^:node root-node (first (get-in modifier-map [lang [modifier-cat child-cat]]))]
                        [^:edge root-node modifier {:role :arg :index 0 :category modifier-cat}]
                        [^:edge root-node child {:role :arg :index 1 :category child-cat}])))))))
          g
          (filter #(= :modifier (:type (attrs g %)))
                  (alg/post-traverse g))))
