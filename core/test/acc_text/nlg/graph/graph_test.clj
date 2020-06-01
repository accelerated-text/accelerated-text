(ns acc-text.nlg.graph.graph-test
  (:require [acc-text.nlg.graph.amr :refer [attach-amrs]]
            [acc-text.nlg.graph.lists :refer [resolve-lists subset-of-edges-from]]
            [acc-text.nlg.graph.polarity :refer [resolve-polarity]]
            [acc-text.nlg.graph.data :refer [resolve-data]]
            [acc-text.nlg.graph.utils :as utils :refer [ubergraph->semantic-graph]]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :refer [semantic-graph->ubergraph]]
            [acc-text.nlg.test-utils :refer [load-test-context load-test-semantic-graph]]
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

(defn fattrs [g node] (->> node (first) (uber/attrs g)))

(defn load-graph [sg-ctx-file]
  (let [ctx (load-test-context sg-ctx-file)]
    (-> (load-test-semantic-graph sg-ctx-file)
        (semantic-graph->ubergraph)
        (attach-amrs ctx)
        (resolve-lists)
        (resolve-data ctx))))

(deftest list-with-quotes-attached
  (let [g     (load-graph "one-of-with-str")
        red   (-> g (utils/find-nodes {:value "red"}) (ffirst))
        green (-> g (utils/find-nodes {:value "green"}) (ffirst))

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
    (is (= {:category "A" :type :synonyms} (fattrs g list->mkA-red)))
    (is (= {:category "A" :type :synonyms} (fattrs g list->mkA-green)))
    (is (= 1 (count list->mkA-red)))
    (is (= 1 (count list->mkA-green)))
    (is (= (first list->mkA-red) (first list->mkA-green)))

    ;;list is connected with mkAP
    (is (= {:type :operation :name "mkAP" :category "AP" :module "Syntax"}
           (fattrs g mkAP->list-red)))
    (is (= {:type :operation :name "mkAP" :category "AP" :module "Syntax"}
           (fattrs g mkAP->list-green)))
    (is (= (first mkAP->list-red) (first mkAP->list-green)))))

(deftest list-with-the-quotes-attached
  ;;Different from the test above in that that data nodes will have determiners
  ;;in front of them. That is not 'Apple is red' but 'Murderer is in *the* city'
  (let [g    (load-graph "one-of-with-the-str")
        city (-> g (utils/find-nodes {:value "city"}) (ffirst))
        town (-> g (utils/find-nodes {:value "town"}) (ffirst))

        mkNP->city (utils/get-predecessors g city)
        mkNP->town (utils/get-predecessors g town)

        det->city (subset-of-edges-from g (first mkNP->city) #{city})
        det->town (subset-of-edges-from g (first mkNP->town) #{town})]

    (is (not (nil? city)))
    (is (not (nil? town)))
    (is (= {:type :operation :name "mkNP" :category "NP" :module "Syntax"} (fattrs g mkNP->town)))
    (is (= {:type :operation :name "mkNP" :category "NP" :module "Syntax"} (fattrs g mkNP->city)))
    ;;that's the main point of the test - check if mkNP did not loose the Dets
    (is (= {:type :operation :name "the_Det" :category "Det" :module "Syntax"}
           (->> det->city first :dest (uber/attrs g))))
    (is (= {:type :operation :name "the_Det" :category "Det" :module "Syntax"}
           (->> det->town first :dest (uber/attrs g))))))

(deftest ap-attached-to-segment
  (let [g (load-graph "ap-no-amr")
        white (-> g (utils/find-nodes {:category "A"}) first)
        fridge (-> g (utils/find-nodes {:category "N"}) first)]
    (is (= ["white"] (-> white second :forms)))
    (is (= :dictionary-item (-> white second :type)))
    (is (= ["fridge" "fridges"] (-> fridge second :forms)))
    (is (= :dictionary-item (-> fridge second :type)))))
