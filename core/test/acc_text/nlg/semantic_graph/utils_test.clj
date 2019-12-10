(ns acc-text.nlg.semantic-graph.utils-test
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest is are]]))

(deftest concept-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [types ids]
      (= ids (sg-utils/find-concept-ids semantic-graph types))
      #{:dictionary-item} #{:04 :07}
      #{:data} #{:06 :08}
      #{:amr} #{:03}
      #{:sequence} #{})))

(deftest child-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [parent-ids child-ids]
      (= child-ids (sg-utils/find-child-ids semantic-graph parent-ids))
      #{:01} #{:02}
      #{:02} #{:03}
      #{:03} #{:04 :05 :08}
      #{:04 :05} #{:06 :07}
      #{:06 :07} #{}
      #{:08} #{}
      #{} #{})))

(deftest descendant-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [parent-ids descendant-ids]
      (= descendant-ids (sg-utils/find-descendant-ids semantic-graph parent-ids))
      #{:01} #{:02 :03 :04 :05 :06 :07 :08}
      #{:02} #{:03 :04 :05 :06 :07 :08}
      #{:03} #{:04 :05 :06 :07 :08}
      #{:04 :05} #{:06 :07}
      #{:06 :07} #{}
      #{:08} #{}
      #{:09} #{}
      #{} #{})))

(deftest child-with-relation-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [concept role child]
      (= child (sg-utils/get-child-with-relation semantic-graph concept role))
      {:id :05 :type :data :value "authors"} :modifier {:id :07 :type :dictionary-item :value "good"}
      {:id :02 :type :segment} :function nil)))

(deftest child-concept-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [concept children]
      (= children (sg-utils/get-children semantic-graph concept))
      {:id :03 :type :amr :value "author"} [{:id :04 :type :dictionary-item :value "written"}
                                            {:id :05 :type :modifier}
                                            {:id :08 :type :data :value "title"}]
      {:id :07 :type :data :value "title"} [])))

(deftest concept-with-type-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [type concepts]
      (= concepts (sg-utils/get-concepts-with-type semantic-graph type))
      :data [{:id :06 :type :data :value "authors"}
             {:id :08 :type :data :value "title"}]
      :boolean [])))

(deftest graph-pruning
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (is (= #::sg{:concepts  []
                 :relations []}
           (sg-utils/prune-branches semantic-graph #{:01})))
    (is (= #::sg{:concepts  [{:id   :01
                              :type :document-plan}]
                 :relations []}
           (sg-utils/prune-branches semantic-graph #{:02})))
    (is (= #::sg{:concepts  [{:id :01 :type :document-plan}
                             {:id :02 :type :segment}]
                 :relations [{:from :01 :to :02 :role :segment}]}
           (sg-utils/prune-branches semantic-graph #{:03})))
    (is (= semantic-graph
           (sg-utils/prune-branches semantic-graph #{:09})))))

(deftest unrelated-branch-pruning
  (let [semantic-graph (utils/load-test-semantic-graph "variable-unused")]
    (is (= #::sg{:concepts  [{:id :01 :type :document-plan}
                             {:id :04 :type :segment}
                             {:id :05 :type :quote :value "some text"}]
                 :relations [{:from :01 :to :04 :role :segment}
                             {:from :04 :to :05 :role :instance}]}
           (sg-utils/prune-unrelated-branches semantic-graph)))))
