(ns acc-text.nlg.graph.graph-test
  (:require [acc-text.nlg.graph.amr :refer [attach-amrs]]
            [acc-text.nlg.graph.lists :refer [resolve-lists]]
            [acc-text.nlg.graph.polarity :refer [resolve-polarity]]
            [acc-text.nlg.graph.utils :as utils :refer [ubergraph->semantic-graph]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [acc-text.nlg.test-utils
             :refer
             [load-test-context load-test-semantic-graph]]
            [clojure.test :refer [deftest is testing]]
            [ubergraph.core :as uber]))

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

(defn- fattrs [g node] (->> node (first) (uber/attrs g)))

(deftest list-with-quotes-attached
  (let [context        (load-test-context "one-of-with-str")
        semantic-graph (load-test-semantic-graph "one-of-with-str")
        g              (-> semantic-graph
                           (semantic-graph->ubergraph)
                           (attach-amrs context)
                           (resolve-lists))
        red            (-> g (utils/find-nodes {:value "red"}) (ffirst))
        green          (-> g (utils/find-nodes {:value "green"}) (ffirst))

        mkA->red   (utils/get-predecessors g red)
        mkA->green (utils/get-predecessors g green)

        list->mkA-red    (utils/get-predecessors g (first mkA->red))
        list->mkA-green  (utils/get-predecessors g (first mkA->green))
        mkAP->list-red   (utils/get-predecessors g (first list->mkA-red))
        mkAP->list-green (utils/get-predecessors g (first list->mkA-green))]

    ;; data nodes must exist and be leaves
    (is (not (nil? red)))
    (is (not (nil? green)))
    (is (empty? (utils/get-successors g red)))
    (is (empty? (utils/get-successors g green)))

    ;;mkA must connect to data
    (is (= {:type :operation :name "mkA" :category "A" :module "ParadigmsEng"}
           (fattrs g mkA->red)))
    (is (= {:type :operation :name "mkA" :category "A" :module "ParadigmsEng"}
           (fattrs g mkA->green)))
    (is (= 1 (count mkA->red)))
    (is (= 1 (count mkA->green)))

    ;;single list points to both mkAs
    (is (= {:type :synonyms} (fattrs g list->mkA-red)))
    (is (= {:type :synonyms} (fattrs g list->mkA-green)))
    (is (= 1 (count list->mkA-red)))
    (is (= 1 (count list->mkA-green)))
    (is (= (first list->mkA-red) (first list->mkA-green)))

    ;;list is connected with mkAP
    (is (= {:type :operation :name "mkAP" :category "AP" :module "Syntax"}
           (fattrs g mkAP->list-red)))
    (is (= {:type :operation :name "mkAP" :category "AP" :module "Syntax"}
           (fattrs g mkAP->list-green)))
    (is (= (first mkAP->list-red) (first mkAP->list-green)))))
