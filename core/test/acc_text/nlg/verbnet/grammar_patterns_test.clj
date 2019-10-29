(ns acc-text.nlg.verbnet.grammar-patterns-test
  (:require [acc-text.nlg.verbnet.grammar-patterns :as sut]
            [clojure.test :refer [deftest is]]))

(deftest vnet-id->predicate
  (is (= "aaa" (sut/drop-vnet-id "aaa")))
  (is (= "aaa" (sut/drop-vnet-id "aaa-1")))
  (is (= "aaa" (sut/drop-vnet-id "aaa-1.1"))))

