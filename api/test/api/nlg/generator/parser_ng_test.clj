(ns api.nlg.generator.parser-ng-test
  (:require [api.nlg.generator.parser-ng :as sut]
            [api.test-utils :refer [load-test-document-plan]]
            [clojure.test :refer [deftest is]]))

(deftest simple-plan-parser
  (is (= [[[{:dynamic [{:name  {:cell :product-name :dyn-name "$1"}
                        :attrs {:type :product :source :cell}}]}]]]
         (sut/parse-document-plan (load-test-document-plan "single-subj") nil nil)))

  (is (= [[[{:dynamic [{:name  {:cell :product-name :dyn-name "$1"}
                        :attrs {:source :cell
                                :type :cell}}]}]]]
         (sut/parse-document-plan (load-test-document-plan "simple-plan") nil nil))))
