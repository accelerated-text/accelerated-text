(ns acc-text.nlg.graph.segment
  (:require [acc-text.nlg.graph.utils :as utils]
            [ubergraph.core :as uber]
            [clojure.tools.logging :as log])
  (:import (java.util UUID)))

(defn get-instance-indices [g node]
  (map #(let [[_ _ {index :index}] (uber/edge-with-attrs g (utils/get-in-edge g %))] index)
       (utils/get-successors g node)))

(defn add-paragraph-quote [g]
  (let [quote-node (UUID/randomUUID)]
      (reduce (fn [g [node _]]
                (if-let [index (some->> (get-instance-indices g node) (seq) (apply max) (inc))]
                  (utils/add-edges g [[^:edge node quote-node {:role :instance :index index}]])
                  g))
              (uber/add-nodes-with-attrs g [quote-node {:type :quote :value "¶" :category "Str"}])
              (utils/find-nodes g {:type :segment}))))
