(ns api.nlg.generator.realizer-test
  (:require [api.nlg.generator.realizer :as realizer]
            [clojure.test :refer [deftest is]]))

(deftest replace-quote-test
  (let [sent "$1 - Albert Einstein"
        context {:dynamic [{:name {:dyn-name "$1" :quote "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former"} :attrs {:source :quote}}]}
        data {}
        expected "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former - Albert Einstein"
        result (first (realizer/realize data {:context   context
                                              :templates [sent]}))]
    (is (= expected result))))

(deftest replace-cell-test
  (let [sent "$1 - Albert Einstein"
        context {:dynamic [{:name {:dyn-name "$1" :cell :quote} :attrs {:source :cell}}]}
        data {:quote "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former"}
        expected "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former - Albert Einstein"
        result (first (realizer/realize data {:context   context
                                              :templates [sent]}))]
    (is (= expected result))))

(deftest join-sentences-test
  (let [s1 "Hello world"
        s2 "it's a very hot day"
        result (realizer/join-sentences [s1 s2])]
    (is (= "Hello world. It's a very hot day." result))))
