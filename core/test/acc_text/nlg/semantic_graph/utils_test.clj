(ns acc-text.nlg.semantic-graph.utils-test
  (:require [clojure.test :refer [deftest is are]]
            [acc-text.nlg.test-utils :as utils]
            [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.utils :as sg-utils]))

(deftest child-search
  (let [semantic-graph (utils/load-test-semantic-graph "author-amr-with-adj")]
    (are [parent-ids child-ids]
      (= child-ids (sg-utils/find-children semantic-graph parent-ids))
      #{:01} #{:02 :03 :04 :05 :06 :07}
      #{:02} #{:03 :04 :05 :06 :07}
      #{:03} #{:04 :05 :06 :07}
      #{:04 :05} #{:06}
      #{:06 :07} #{}
      #{:08} #{}
      #{} #{})))

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
