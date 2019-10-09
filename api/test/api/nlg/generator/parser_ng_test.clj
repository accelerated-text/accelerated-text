(ns api.nlg.generator.parser-ng-test
  (:require [api.nlg.generator.parser-ng :as sut]
            [api.test-utils :refer [load-test-data]]
            [clojure.test :refer [deftest is]]))

(deftest simple-plan-parser
  (is (= [[[{:dynamic [{:name  {:cell :product-name :dyn-name "$1"}
                        :attrs {:type :product :source :cell}}]}]]]
         (sut/parse-document-plan (load-test-data "single-subj") nil nil)))

  (is (= [[{:dynamic [{:name  {:cell :product-name :dyn-name "$1"}
                       :attrs {:source :cell}}]}]]
         (sut/parse-document-plan (load-test-data "simple-plan") nil nil))))
