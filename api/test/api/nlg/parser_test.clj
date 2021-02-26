(ns api.nlg.parser-test
  (:require [acc-text.nlg.semantic-graph :as sg]
            [api.db-fixtures :as db]
            [api.nlg.parser :as parser]
            [clojure.set :as set]
            [clojure.test :refer [deftest testing is use-fixtures]]))

(use-fixtures :each db/clean-db)

(deftest document-plan-parsing
  (testing "Simple document plan parsing"
    (let [document-plan {:documentPlan {:segments [{:children [{:child  {:name "title" :type "Cell"}
                                                                :name   "good"
                                                                :type   "Dictionary-item-modifier"
                                                                :itemId "NN-good"}]
                                                    :type     "Segment"}]
                                        :type     "Document-plan"}}
          {::sg/keys [concepts relations]} (parser/document-plan->semantic-graph document-plan)]
      (is (set/subset? #{:document-plan :segment :data :dictionary-item} (set (map :type concepts))))
      (is (set/subset? #{:segment :instance :modifier} (set (map :role relations))))))
  (testing "AMR parsing"
    (let [document-plan {:documentPlan {:roles     [{:children [{:name   "written"
                                                                 :type   "Dictionary-item"
                                                                 :itemId "written"}]
                                                     :name     "lexicon"}
                                                    {:children [{:name "authors" :type "Cell"}]
                                                     :name     "agent"}
                                                    {:children [{:name "title" :type "Cell"}]
                                                     :name     "co-agent"}
                                                    {:children [nil] :name "theme"}]
                                        :type      "AMR"
                                        :conceptId "author"}}
          {::sg/keys [concepts relations]} (parser/document-plan->semantic-graph document-plan)]
      (is (set/subset? #{:amr :data :dictionary-item} (set (map :type concepts))))
      (is (set/subset? #{:arg} (set (map :role relations))))))
  (testing "Modifier parsing"
    (let [document-plan {:documentPlan {:name   "good"
                                        :type   "Dictionary-item-modifier"
                                        :child  {:name   "famous"
                                                 :type   "Dictionary-item-modifier"
                                                 :child  {:name "authors" :type "Cell"}
                                                 :itemId "famous"}
                                        :itemId "good"}}
          {::sg/keys [concepts relations]} (parser/document-plan->semantic-graph document-plan)]
      (is (set/subset? #{:data :dictionary-item} (set (map :type concepts))))
      (is (set/subset? #{:modifier} (set (map :role relations))))
      (is (= 3 (count relations))))))
