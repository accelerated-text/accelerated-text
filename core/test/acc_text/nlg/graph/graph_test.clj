(ns acc-text.nlg.graph.graph-test
  (:require [acc-text.nlg.graph.amr :refer [attach-amrs]]
            [acc-text.nlg.graph.polarity :refer [resolve-polarity]]
            [acc-text.nlg.graph.utils :refer [ubergraph->semantic-graph]]
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

(defn concept-with-val [concepts]
  (some #(when (= "red" (::sg/value %)) %) concepts))

(deftest synonyms
  (let [{:keys [acc-text.nlg.semantic-graph/relations acc-text.nlg.semantic-graph/concepts]}
        (-> "one-of-with-str" load-test-context  :amr  (get-in ["ZlmgilOQpBKynpTm" :semantic-graph]))
        red (concept-with-val concepts)
        green (some #(when (= "green" (:value %)) %) concepts)
        ]
    (is (= nil red))
    ))
