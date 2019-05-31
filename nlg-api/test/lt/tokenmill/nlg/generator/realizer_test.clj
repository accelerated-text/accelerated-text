(ns lt.tokenmill.nlg.generator.realizer-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.realizer :refer :all]))

(deftest replace-quote-test
  (let [sent "$1 - Albert Einstein"
        context {:dynamic [{:name {:dyn-name "$1" :quote "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former"} :attrs {:source :quote}}]}
        data {}
        expected "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former - Albert Einstein"
        result (first (realize data {:context context
                                     :templates [sent]}))]
    (is (= expected result))))

(deftest replace-cell-test
  (let [sent "$1 - Albert Einstein"
        context {:dynamic [{:name {:dyn-name "$1" :cell :quote} :attrs {:source :cell}}]}
        data {:quote "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former"}
        expected "Only two things are infinite, the universe and human stupidity, and I'm not sure about the former - Albert Einstein"
        result (first (realize data {:context context
                                     :templates [sent]}))]
    (is (= expected result))))
