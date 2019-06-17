(ns nlg.generator.parser
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [cheshire.core :as ch]
            [nlg.generator.ops :as ops]
            [clojure.string :as str]
            [nlg.api.lexicon :as lexicon]))

(defn node-plus-children [node children]
  (cons node children))

(declare parse-node)

(defn parse-segment
  [node]
  (let [children (node :children)]
    (map parse-node children)))

(defn parse-component
  [node]
  (let [name (parse-node (node :name))
        children (flatten (map parse-node (node :children)))]
    (node-plus-children (ops/set-subj name) children)))

(defn parse-product [node] (parse-component node))

(defn parse-relationship
  [node]
  (let [rel-name (node :relationshipType)
        children (flatten (map parse-node (node :children)))]
    (node-plus-children (ops/set-verb-static (ops/get-word rel-name)) (map ops/set-obj children))))

(defn parse-lexicon
  [node]
  (let [word (get node :text)
        response (-> {:query (str/lower-case word)} (lexicon/search nil) (get-in [:body :items]))
        synonyms (set (mapcat :synonyms response))]
    (fn [_]
      (let [synonym (first (shuffle synonyms))]
        (if (nil? synonym)
          (do (log/debugf "No entries for '%s' found in lexicon. Returning original word." word) word)
          (do (log/debugf "Searching for: '%s'. Result: %s" word, synonym) synonym))))))

(defn parse-rhetorical [node]
  (let [rst-type (node :rstType)
        children (map parse-node (node :children))]
    (map ops/set-complement children)))

(defn parse-cell
  [node]
  (let [cell-name (keyword (node :name))]
    (fn [data]
      (let [result (get data cell-name)]
        (log/debugf "Searching for: '%s' in %s. Result: %s" cell-name, data, result)
        result))))

(defn parse-quote
  [node]
  (fn [_] (node :text)))

(defn parse-document-plan
  [node]
  (let [statements (node :segments)]
    (doall (map parse-node statements))))


(defn parse-if-statement
  [node]
  (log/debugf "Node: %s" node)
  (let [op (keyword (node :operator))
        t (keyword (node :type))
        cond-fn (case op
                  :== (partial =)
                  := (partial =))]
    (case t
      :Value-comparison (fn [data]
                          (let [v1 (parse-node (node :value1))
                                v2 (parse-node (node :value2))]
                            (cond-fn (v1 data) (v2 data)))))))

(defn parse-condition
  [node]
  (let [t (keyword (node :type))]
    (case t
      :If-condition {:if (parse-if-statement (node :condition)) :then (parse-node (node :thenExpression
                                                                                        ))}
      :Default-condition {:if (fn [_] true) :then (parse-node (node :thenExpression))})))

(defn resolve-cond-seq
  [conds]
  (when (not (empty? conds))
    (let [head (first conds)
          tail (rest conds)
          if-statement (head :if)
          then-statement (head :then)]
      (log/debugf "Resolving condition. Head: %s\n" head)
      (ops/lazy-if if-statement then-statement (resolve-cond-seq tail)))))
  

(defn parse-conditional
  [node]
  (let [conditions (map parse-condition (node :conditions))]
    (list (resolve-cond-seq conditions))))

(defn parse-list
  [opts node]
  (let [children (node :children)
        results (case (opts :type)
                  :Any-of (parse-node (rand-nth children)))]
    results))

(defn parse-node
  [node]
  (let [t (keyword (node :type))]
    (case t
      :Document-plan (parse-document-plan node)
      :Segment (parse-segment node)
      :Product (parse-product node)
      :Product-component (parse-component node)
      :Cell (parse-cell node)
      :Quote (parse-quote node)
      :Relationship (parse-relationship node)
      :Rhetorical (parse-rhetorical node)
      :If-then-else (parse-conditional node)
      :One-of-synonyms (parse-list {:type :Any-of} node)
      :Lexicon (parse-lexicon node))))

