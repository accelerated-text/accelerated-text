(ns acc-text.nlg.graph.graph-test
  (:require [acc-text.nlg.graph.amr :refer [attach-amrs]]
            [acc-text.nlg.graph.polarity :refer [resolve-polarity]]
            [acc-text.nlg.graph.lists :refer [resolve-lists]]
            [acc-text.nlg.graph.utils :refer [ubergraph->semantic-graph save-graph]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [acc-text.nlg.test-utils :refer [load-test-semantic-graph load-test-context]]
            [clojure.test :refer [deftest is testing]]))

(deftest polarity
  (let [semantic-graph (load-test-semantic-graph "polarity-test")]
    (testing "Positive polarity"
      (let [context (load-test-context "polarity-positive-test")]
        (is (= 2 (count (filter (fn [{:keys [type name]}]
                                  (and (= type :operation) (= name "positivePol")))
                                (-> semantic-graph
                                    (semantic-graph->ubergraph)
                                    (attach-amrs context)
                                    (resolve-polarity)
                                    (ubergraph->semantic-graph)
                                    (get ::sg/concepts))))))))
    (testing "Negative polarity"
      (let [context (load-test-context "polarity-negative-test")]
        (is (= 2 (count (filter (fn [{:keys [type name]}]
                                  (and (= type :operation) (= name "negativePol")))
                                (-> semantic-graph
                                    (semantic-graph->ubergraph)
                                    (attach-amrs context)
                                    (resolve-polarity)
                                    (ubergraph->semantic-graph)
                                    (get ::sg/concepts))))))))))

(defn concept-with-val [concepts val]
  (some #(when (= val (:value %)) %) concepts))

(defn foo []
  (let [context        (load-test-context "one-of-with-str")
        semantic-graph (load-test-semantic-graph "one-of-with-str")
        {:keys [acc-text.nlg.semantic-graph/relations
                acc-text.nlg.semantic-graph/concepts]}
        (-> semantic-graph
            (semantic-graph->ubergraph)
            (attach-amrs context)
            (resolve-lists)
            (ubergraph->semantic-graph))
        red            (concept-with-val concepts "red")
        green          (concept-with-val concepts "green")
        ]
    (-> semantic-graph
        (semantic-graph->ubergraph)
        (attach-amrs context)
        (resolve-lists)
        (ubergraph.core/viz-graph {:auto-label true}))

    ))
