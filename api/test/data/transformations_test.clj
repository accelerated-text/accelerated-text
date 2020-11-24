(ns data.transformations-test
  (:require  [clojure.test :refer [deftest is]]
             [api.nlg.enrich.data.transformations :as sut]))

(deftest cell-approximation
  (is (= "" (sut/number-approximation
             nil
             {:scale      100 :language :en
              :formatting :numberwords.domain/words
              :relation   :numberwords.domain/around})))
  (is (= "around 1k" (sut/number-approximation
              "1040"
              {:scale      1000 :language :en
               :formatting :numberwords.domain/bites
               :relation   :numberwords.domain/around}))))

(deftest cell-cleanup
  (is (= "product" (sut/cleanup "product (122)" {:regex #" \(.*\)" :replace ""}))))

(deftest cell-add-symbol
  (is (= "-$10" (sut/add-symbol "-10" {:symbol "$" :position :front :skip #{\- \+}}))))

