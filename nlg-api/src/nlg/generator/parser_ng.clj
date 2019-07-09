(ns nlg.generator.parser-ng
  (:require [clojure.tools.logging :as log]
            [nlg.generator.ops :as ops]
            [clojure.string :as str]
            [nlg.api.lexicon :as lexicon]
            [nlg.api.dictionary :as dictionary-api]
            [nlg.generator.realizer :as realizer]
            [data-access.entities.amr :as amr-entity]))

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
  ;; TODO: deprecate
  [node attrs ctx]
  (let [word (get node :text)
        response (-> {:query (str/lower-case word) :profile (ctx :reader-profile)} (lexicon/search nil) (get-in [:body :items]))
        synonyms (set (mapcat :synonyms response))]
    (map #(ops/append-dynamic % (merge attrs {:type :wordlist :class word}) ctx) synonyms)))

(defn parse-dictionary
  [node attrs ctx]
  (let [key (get node :name)
        reader-profile (ctx :reader-profile)
        words (dictionary-api/search (str/lower-case key) reader-profile)]
    (map #(ops/append-dynamic % (merge attrs {:type :wordlist :class key}) ctx) words)))

(defn parse-rhetorical [node attrs ctx]
  (let [children (map parse-node (node :children) attrs ctx)]
    (map ops/set-complement children)))

(defn parse-rst
  [node attrs ctx]
  ())

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

(defn parse-if-statement
  [node attrs ctx]
  (log/tracef "Node: %s" node)
  (let [op (keyword (node :operator))
        t (keyword (node :type))
        cond-fn (case op
                  :== (partial =)
                  := (partial =))]
    (case t
      :Value-comparison (fn [data]
                          (let [v1 (realizer/get-value (-> (parse-node (node :value1) attrs ctx)
                                                           (:dynamic)
                                                           (first)) data)
                                v2 (realizer/get-value (-> (parse-node (node :value2) attrs ctx)
                                                           (:dynamic)
                                                           (first)) data)]
                            (log/tracef "Comparing: '%s' vs '%s'" v1 v2)
                            (cond-fn v1 v2))))))

(defn build-default-cond
  [node attrs ctx gate]
  (parse-node (node :thenExpression) (assoc attrs :gate gate) ctx))

(defn build-if-cond
  [node attrs ctx]
  (parse-node (node :thenExpression) (assoc attrs :gate (parse-if-statement (node :condition) attrs ctx)) ctx))

(defn parse-conditional
  [node attrs ctx]
  (let [conditions (node :conditions)
        usual-conds (filter #(= :If-condition (keyword (% :type))) conditions) ;; All of the If Statements
        group-negation (fn [data] (not-any? #(% data) (map #(parse-if-statement (% :condition) attrs ctx) usual-conds))) ;; All of the previous IFs must be false in order for else to be true
        default-conds (filter #(= :Default-condition (keyword (% :type))) conditions) ;; Else statement

        block (flatten (map #(build-if-cond % attrs ctx) usual-conds))]
    (if (empty? default-conds)
      block
      (flatten (conj block (build-default-cond (first default-conds) attrs ctx group-negation))))))

(defn parse-list
  [opts node attrs ctx]
  (let [children (node :children)
        results (case (opts :type)
                  :Any-of (parse-node (rand-nth children) attrs ctx))]
    results))

(defn parse-amr
  [node attrs ctx]
  (let [idx (swap! parse-cnt inc)
        vc (amr-entity/get-verbclass (node :id))

        template "<AMR GOES HERE, $4>"
        main (ops/append-dynamic {:quote template :dyn-name (format "$%d" idx) } (assoc attrs :source :quote) ctx)
        children (map #(parse-node % attrs ctx) (node :children))]
    (cons main children)))

(defn parse-themrole
  [node attrs ctx]
  (let [title (:title node)]
    (map #(parse-node % (assoc attrs :title title) ctx) (:children node))))

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
      :Dictionary-item (parse-dictionary node attrs ctx)
      :RST (parse-rst node attrs ctx)
      :AMR (parse-amr node attrs ctx)
      :Thematic-role (parse-themrole node attrs ctx)
      (parse-unknown node))))



