(ns acc-text.nlg.graph.condition-test
  (:require [acc-text.nlg.graph.condition :as condition]
            [acc-text.nlg.graph.utils :refer [ubergraph->semantic-graph prune-graph]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [acc-text.nlg.test-utils :refer [load-test-semantic-graph]]
            [clojure.test :refer [deftest testing is are]]))

(deftest arg-normalization
  (is (= [nil "" "abc" 5M 1.0M 5M 1.0M] (condition/normalize [nil "" "abc" "5" "1.0" 5 1.0]))))

(deftest comparison
  (testing "Result is true"
    (are [operator args]
      (true? (condition/comparison operator args))
      "=" [1 "1" "1.0"]
      "=" ["" "" " "]
      "=" ["Abc" "Abc"]
      "=" [1 "1" "1.0"]
      "==" [1 "1" "1.0"]
      "==" ["" "" " "]
      "==" ["Abc" "Abc"]
      "==" [1 "1" "1.0"]
      "!=" [1 "1" "1.1"]
      "<" [5 "6" "7"]
      "<=" [5 "5"]
      "<=" [4 "5"]
      ">" [2 "1.1" "1"]
      ">=" [2 "2"]
      ">=" [2.1 "2"]))
  (testing "Result is false"
    (are [operator args]
      (false? (condition/comparison operator args))
      "=" [1 "1" "1.0001"]
      "=" ["" " " "."]
      "=" ["Abc" "abc"]
      "=" [1 2]
      "==" [1 "1" "1.0001"]
      "==" ["" " " "."]
      "==" ["Abc" "abc"]
      "==" [1 2]
      "!=" [1 "1"]
      "<" [7 "6"]
      "<=" [5 "4.99"]
      "<=" [5.1 "5"]
      ">" [2 "1.1" "1.11"]
      ">=" ["1.99" 2]
      ">=" [1.99 "2"]))
  (testing "Edge cases"
    (is (nil? (condition/comparison "=" [])))))

(deftest conditions
  (let [context {:data {"0" "0" "1" "1" "A" "A" "B" "B"}}]
    (testing "If-then"
      (let [semantic-graph (load-test-semantic-graph "if-then-branch")]
        (is (= #{"TRUE"} (->> (-> semantic-graph
                                  (semantic-graph->ubergraph)
                                  (condition/determine-conditions context)
                                  (prune-graph)
                                  (ubergraph->semantic-graph)
                                  (get ::sg/concepts))
                              (filter #(= :quote (:type %)))
                              (map :value)
                              (into #{}))))))
    (testing "If-else"
      (let [semantic-graph (load-test-semantic-graph "if-else-branch")]
        (is (= #{"FALSE"} (->> (-> semantic-graph
                                   (semantic-graph->ubergraph)
                                   (condition/determine-conditions context)
                                   (prune-graph)
                                   (ubergraph->semantic-graph)
                                   (get ::sg/concepts))
                               (filter #(= :quote (:type %)))
                               (map :value)
                               (into #{}))))))
    (testing "Complex if"
      (let [semantic-graph (load-test-semantic-graph "if-complex")]
        (is (= #{"TRUE"} (->> (-> semantic-graph
                                  (semantic-graph->ubergraph)
                                  (condition/determine-conditions context)
                                  (prune-graph)
                                  (ubergraph->semantic-graph)
                                  (get ::sg/concepts))
                              (filter #(= :quote (:type %)))
                              (map :value)
                              (into #{}))))))
    (testing "Data if"
      (let [semantic-graph (load-test-semantic-graph "if-data")]
        (is (= #{"TRUE"} (->> (-> semantic-graph
                                  (semantic-graph->ubergraph)
                                  (condition/determine-conditions context)
                                  (prune-graph)
                                  (ubergraph->semantic-graph)
                                  (get ::sg/concepts))
                              (filter #(= :quote (:type %)))
                              (map :value)
                              (into #{}))))))))
