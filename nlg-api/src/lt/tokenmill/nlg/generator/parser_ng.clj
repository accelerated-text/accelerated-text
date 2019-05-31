(ns lt.tokenmill.nlg.generator.parser-ng
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [cheshire.core :as ch]
            [lt.tokenmill.nlg.generator.ops :as ops]
            [clojure.string :as str]
            [lt.tokenmill.nlg.api.lexicon :as lexicon]))

(def parse-cnt (atom 0))
(defn reset-parse-cnt [] (reset! parse-cnt 0))

(defn node-plus-children [node children attrs ctx]
  (cons node children))

(declare parse-node)

(defn parse-segment
  [node attrs ctx]
  (let [children (map #(parse-node % attrs ctx) (node :children))]
    (log/debug "Children" children)
    children))

(defn parse-component
  [node attrs ctx]
  (let [main (parse-node (node :name) (assoc attrs :type :component) ctx)
        children (map #(parse-node % attrs ctx) (node :children))]
    (cons main children)))


(defn parse-product
  [node attrs ctx]
  (let [main (parse-node (node :name) (assoc attrs :type :product) ctx)
        children (flatten (map #(parse-node % attrs ctx) (node :children)))]
    (cons main children)))

(defn parse-relationship
  [node attrs ctx]
  (let [rel-name (node :relationshipType)
        main (ops/append-static (ops/get-word rel-name) ctx)
        children (map #(parse-node % (assoc attrs :type :benefit) ctx) (node :children))]
    (cons main children)))

(defn parse-lexicon
  [node attrs ctx]
  (let [word (get node :text)
        response (-> {:query (str/lower-case word)} (lexicon/search nil) (get-in [:body :items]))
        synonyms (set (mapcat :synonyms response))]
    (map #(ops/append-dynamic % (merge attrs {:type :wordlist :class word}) ctx) synonyms)))

(defn parse-rhetorical [node attrs ctx]
  (let [rst-type (node :rstType)
        children (map parse-node (node :children) attrs ctx)]
    (map ops/set-complement children)))

(defn parse-rst
  [node attrs ctx]
  (let [rst-type (node :rstType)
        nucleus (node :nucleus)
        satellite (node :satellite)]
    ))

(defn parse-cell
  [node attrs ctx]
  (let [cell-name (keyword (node :name))
        idx (swap! parse-cnt inc)]
    (ops/append-dynamic {:cell cell-name :dyn-name (format "$%d" idx)} (assoc attrs :source :cell) ctx)))

(defn parse-quote
  [node attrs ctx]
  (let [idx (swap! parse-cnt inc)]
    (ops/append-dynamic {:quote (node :text) :dyn-name (format "$%d" idx) } (assoc attrs :source :quote) ctx)))
  
(defn parse-document-plan
  [node attrs ctx]
  (let [_ (reset-parse-cnt)
        statements (node :segments)
        children (map #(parse-node % attrs ctx) statements)]
    (log/debug "Parsed statements: " children)
    children))

(defn get-value
  [item data]
  (let [t ((item :attrs) :source)]
    (case t
      :cell (get data ((item :name) :cell))
      :quote ((item :name) :quote))))

(defn parse-if-statement
  [node attrs ctx]
  (log/debugf "Node: %s" node)
  (let [op (keyword (node :operator))
        t (keyword (node :type))
        cond-fn (case op
                  :== (partial =)
                  := (partial =))]
    (case t
      :Value-comparison (fn [data]
                          (let [v1 (get-value (parse-node (node :value1) attrs ctx) data)
                                v2 (get-value (parse-node (node :value2) attrs ctx) data)]
                            (log/debugf "Comparing: '%s' vs '%s'" v1 v2)
                            (cond-fn (v1 data) (v2 data)))))))

(defn parse-condition
  [node attrs ctx]
  (let [t (keyword (node :type))
        if-fn (fn [_] true)]
    (case t
      :If-condition (parse-node (node :thenExpression) (assoc attrs :gate (parse-if-statement (node :condition) attrs ctx)) ctx)
      :Default-condition (parse-node (node :thenExpression) (assoc attrs :gate (fn [_] true)) ctx)
      )))

(defn parse-conditional
  [node attrs ctx]
  (let [conditions (node :conditions)]
    (flatten (map #(parse-condition % attrs ctx) conditions))))

(defn parse-list
  [opts node attrs ctx]
  (let [children (node :children)
        results (case (opts :type)
                  :Any-of (parse-node (rand-nth children) attrs ctx))]
    results))

(defn parse-unknown
  [node]
  (log/debugf "Unknown node: %s" node))

(defn parse-node
  [node attrs ctx]
  (let [t (keyword (node :type))]
    (case t
      :Document-plan (parse-document-plan node attrs ctx)
      :Segment (parse-segment node attrs ctx)
      :Product (parse-product node attrs ctx)
      :Product-component (parse-component node attrs ctx)
      :Cell (parse-cell node attrs ctx)
      :Quote (parse-quote node attrs ctx)
      :Relationship (parse-relationship node attrs ctx)
      :Rhetorical (parse-rhetorical node attrs ctx)
      :If-then-else (parse-conditional node attrs ctx)
      :One-of-synonyms (parse-list {:type :Any-of} node attrs ctx)
      :Lexicon (parse-lexicon node attrs ctx)
      :RST (parse-rst node attrs ctx)
      (parse-unknown node))))

