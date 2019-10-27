(ns api.nlg.parser-test
  (:require [api.nlg.parser :as parser]
            [clojure.test :refer [deftest testing is]]
            [clojure.set :as set]))

(deftest document-plan-parsing
  (testing "Simple document plan parsing"
    (let [root {:segments [{:children [{:child  {:name "title" :type "Cell"}
                                        :name   "good"
                                        :type   "Dictionary-item-modifier"
                                        :itemId "NN-good"}]
                            :type     "Segment"}]
                :type     "Document-plan"}
          {:keys [concepts relations]} (parser/parse-document-plan root)]
      (is (set/subset? #{:document-plan :segment :data :dictionary-item} (set (map :type concepts))))
      (is (set/subset? #{:segment :instance :modifier} (set (map :role relations))))))
  (testing "AMR parsing"
    (let [root {:roles          [{:children [{:name "authors" :type "Cell"}]
                                  :name     "agent"}
                                 {:children [{:name "title" :type "Cell"}]
                                  :name     "co-agent"}
                                 {:children [nil] :name "theme"}]
                :dictionaryItem {:name   "written"
                                 :type   "Dictionary-item"
                                 :itemId "written"}
                :type           "AMR"
                :conceptId      "author"}
          {:keys [concepts relations]} (parser/parse-document-plan root)]
      (is (set/subset? #{:data :dictionary-item} (set (map :type concepts))))
      (is (set/subset? #{:ARG0 :ARG1} (set (map :role relations))))))
  (testing "Modifier parsing"
    (let [root {:name   "good"
                :type   "Dictionary-item-modifier"
                :child  {:name   "famous"
                         :type   "Dictionary-item-modifier"
                         :child  {:name "authors" :type "Cell"}
                         :itemId "famous"}
                :itemId "good"}
          {:keys [concepts relations]} (parser/parse-document-plan root)]
      (is (set/subset? #{:data :dictionary-item} (set (map :type concepts))))
      (is (set/subset? #{:modifier} (set (map :role relations))))
      (is (= 2 (count relations))))))
