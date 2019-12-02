(ns acc-text.nlg.semantic-graph.conditions-test
  (:require [acc-text.nlg.semantic-graph :as sg]
            [acc-text.nlg.semantic-graph.conditions :as conditions]
            [acc-text.nlg.test-utils :as utils]
            [clojure.test :refer [deftest testing is are]]))

(deftest arg-normalization
  (is (= [nil "" "abc" 5M 1.0M 5M 1.0M] (conditions/normalize [nil "" "abc" "5" "1.0" 5 1.0]))))

(deftest comparison
  (testing "Result is true"
    (are [operator args]
      (true? (conditions/comparison operator args))
      "=" [1 "1" "1.0"]
      "=" ["" "" " "]
      "=" ["Abc" "Abc"]
      "=" [1 "1" "1.0"]
      "!=" [1 "1" "1.1"]
      "<" [5 "6" "7"]
      "<=" [5 "5"]
      "<=" [4 "5"]
      ">" [2 "1.1" "1"]
      ">=" [2 "2"]
      ">=" [2.1 "2"]))
  (testing "Result is false"
    (are [operator args]
      (false? (conditions/comparison operator args))
      "=" [1 "1" "1.0001"]
      "=" ["" " " "."]
      "=" ["Abc" "abc"]
      "=" [1 2]
      "!=" [1 "1"]
      "<" [7 "6"]
      "<=" [5 "4.99"]
      "<=" [5.1 "5"]
      ">" [2 "1.1" "1.11"]
      ">=" ["1.99" 2]
      ">=" [1.99 "2"]))
  (testing "Edge cases"
    (is (nil? (conditions/comparison "=" [])))))

(deftest condition-selection
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :04 :type :if-statement}
                           #::sg{:id :08 :type :quote :value "The book was published in 2008."}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :04}
                           #::sg{:from :04 :role :expression :to :08}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-equal-condition")
           {:publishedDate "2008"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :09 :type :default-statement}
                           #::sg{:id :10 :type :quote :value "The book was not published in 2008."}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :09}
                           #::sg{:from :09 :role :expression :to :10}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-equal-condition")
           {:publishedDate "2009"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :04 :type :if-statement}
                           #::sg{:id :12 :type :quote :value "The book was published in 2008 and is about Lucene"}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :04}
                           #::sg{:from :04 :role :expression :to :12}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-with-and")
           {:publishedDate "2008"
            :subtitle      "Lucene, LingPipe, and Gate"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :13 :type :default-statement}
                           #::sg{:id :14 :type :quote :value "The book was not published in 2008 or it is not about Lucene"}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :13}
                           #::sg{:from :13 :role :expression :to :14}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-with-and")
           {:publishedDate "2011"
            :subtitle      "Developing Apps in the New World of Cloud Computing"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :10 :type :default-statement}
                           #::sg{:id :11 :type :quote :value "The book is about computers"}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :10}
                           #::sg{:from :10 :role :expression :to :11}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-not")
           {:categories "Computers"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :04 :type :if-statement}
                           #::sg{:id :09 :type :quote :value "The book is not about computers"}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :04}
                           #::sg{:from :04 :role :expression :to :09}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-not")
           {:categories "Business"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :04 :type :if-statement}
                           #::sg{:id :12 :type :quote :value "Either the book is written in English or it is less than 50 pages long"}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :04}
                           #::sg{:from :04 :role :expression :to :12}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-xor")
           {:language  "en"
            :pageCount "430"})))
  (is (= #::sg{:concepts  [#::sg{:id :01 :type :document-plan}
                           #::sg{:id :02 :type :segment}
                           #::sg{:id :03 :type :condition}
                           #::sg{:id :13 :type :default-statement}
                           #::sg{:id :14 :type :quote :value "Either the book is written in English and it is less than 50 pages long or it is not written in English nor it is less than 50 pages long"}]
               :relations [#::sg{:from :01 :role :segment :to :02}
                           #::sg{:from :02 :role :instance :to :03}
                           #::sg{:from :03 :role :statement :to :13}
                           #::sg{:from :13 :role :expression :to :14}]}
         (conditions/select
           (utils/load-test-semantic-graph "if-xor")
           {:language  "en"
            :pageCount "25"}))))
