(ns acc-text.nlg.graph.segment
  (:require [acc-text.nlg.graph.utils :as utils]
            [ubergraph.core :as uber])
  (:import (java.util UUID)))

(defn get-instance-indices [g node]
  (map #(let [[_ _ {index :index}] (uber/edge-with-attrs g (utils/get-in-edge g %))] index)
       (utils/get-successors g node)))

(defn add-paragraph-symbol [g]
  (let [symbol-node (UUID/randomUUID)]
    (reduce (fn [g [node _]]
              (if-let [index (some->> (get-instance-indices g node) (seq) (apply min) (dec))]
                (utils/add-edges g [[^:edge node symbol-node {:role :instance :index index}]])
                g))
            (uber/add-nodes-with-attrs g [symbol-node {:type :quote :value "¶" :category "Str"}])
            (rest (sort-by :position (utils/find-nodes g {:type :segment}))))))
