(ns acc-text.nlg.semantic-graph.utils-test
  (:require [clojure.test :refer [deftest is are]]
            [acc-text.nlg.test-utils :as utils]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]))

(deftest concept-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [types ids]
      (= ids (sg-utils/find-concept-ids semantic-graph types))
      #{:dictionary-item} #{:04 :06}
      #{:data} #{:05 :07}
      #{:amr} #{:03}
      #{:sequence} #{})))

(deftest child-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [parent-ids child-ids]
      (= child-ids (sg-utils/find-child-ids semantic-graph parent-ids))
      #{:01} #{:02}
      #{:02} #{:03}
      #{:03} #{:04 :05 :07}
      #{:04 :05} #{:06}
      #{:06 :07} #{}
      #{:08} #{}
      #{} #{})))

(deftest descendant-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [parent-ids descendant-ids]
      (= descendant-ids (sg-utils/find-descendant-ids semantic-graph parent-ids))
      #{:01} #{:02 :03 :04 :05 :06 :07}
      #{:02} #{:03 :04 :05 :06 :07}
      #{:03} #{:04 :05 :06 :07}
      #{:04 :05} #{:06}
      #{:06 :07} #{}
      #{:08} #{}
      #{} #{})))

(deftest child-with-relation-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [concept role child]
      (= child (sg-utils/get-child-with-relation semantic-graph concept role))
      #::sg{:id :05 :type :data :value "authors"} :modifier #::sg{:attributes #::sg{:name "good"} :id :06 :type :dictionary-item :value "good"}
      #::sg{:id :02 :type :segment} :function nil)))

(deftest child-concept-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [concept children]
      (= children (sg-utils/get-children semantic-graph concept))
      #::sg{:id :03 :type :amr :value "author"} [#::sg{:id :04 :type :dictionary-item :value "written" :attributes #::sg{:name "written"}}
                                                 #::sg{:id :05 :type :data :value "authors"}
                                                 #::sg{:id :07 :type :data :value "title"}]
      #::sg{:id :07 :type :data :value "title"} [])))

(deftest concept-with-type-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [type concepts]
      (= concepts (sg-utils/get-concepts-with-type semantic-graph type))
      :data [#::sg{:id :05 :type :data :value "authors"}
             #::sg{:id :07 :type :data :value "title"}]
      :boolean [])))

(deftest graph-pruning
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (is (= #::sg{:concepts  []
                 :relations []}
           (sg-utils/prune-branches semantic-graph #{:01})))
    (is (= #::sg{:concepts  [#::sg{:id   :01
                                   :type :document-plan}]
                 :relations []}
           (sg-utils/prune-branches semantic-graph #{:02})))
    (is (= #::sg{:concepts  [#::sg{:id   :01
                                   :type :document-plan}
                             #::sg{:id   :02
                                   :type :segment}]
                 :relations [#::sg{:from :01
                                   :role :segment
                                   :to   :02}]}
           (sg-utils/prune-branches semantic-graph #{:03})))
    (is (= semantic-graph
           (sg-utils/prune-branches semantic-graph #{:08})))))

(deftest unrelated-branch-pruning
  (let [semantic-graph (utils/load-test-semantic-graph "variable-unused")]
    (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                             #::sg{:id :04 :type :segment}
                             #::sg{:id :05 :type :quote :value "some text"}]
                 :relations [#::sg{:from :01 :role :segment :to :04}
                             #::sg{:from :04 :role :instance :to :05}]}
           (sg-utils/prune-unrelated-branches semantic-graph)))))
