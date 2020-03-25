(ns acc-text.nlg.generator-test
  (:require [acc-text.nlg.generator :refer [->abstract ->incomplete ->interface ->resource ->concrete]]
            [clojure.test :refer [deftest is testing]]
            [acc-text.nlg.test-utils :refer [load-test-grammar load-test-syntax]]))

(deftest syntax-generation
  (let [simple-grammar (load-test-grammar "simple")]
    (testing "Abstract syntax"
      (is (= (load-test-syntax "Simple") (->abstract simple-grammar))))
    (testing "Incomplete syntax"
      (is (= (load-test-syntax "SimpleBody") (->incomplete "Eng" simple-grammar))))
    (testing "Interface syntax"
      (is (= (load-test-syntax "SimpleLex") (->interface simple-grammar))))
    (testing "Resource syntax"
      (is (= (load-test-syntax "SimpleLexEng") (->resource "Eng" simple-grammar))))
    (testing "Concrete syntax"
      (is (= (load-test-syntax "SimpleInstance") (->concrete "Eng" simple-grammar))))))
