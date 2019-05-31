(ns lt.tokenmill.nlg.generator.ops-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.generator.ops :as ops]))


(deftest test-merge-contexts
  (testing "merge two simple contexts"
    (let [left {:static ["A"] :dynamic []}
          right {:static ["B", "C"] :dynamic []}
          expected {:static ["A" "B" "C"] :dynamic []}
          result (ops/merge-context left right)]
      (is (= expected result))))
  (testing "merge multiple contexts"
    (let [main {:static [] :dynamic []}
          children [{:static ["A"]}
                    {:static ["B"]}
                    {:static ["C"]}]
          expected {:static ["A" "B" "C"] :dynamic []}
          result (ops/merge-contexts main children)]
      (is (= expected result)))))

(deftest test-join-words
  (testing "join 1 word"
    (is (= "apple" (ops/join-words ["apple"]))))
  (testing "join 2 words"
    (let [data ["apple", "orange"]
          expected "apple and orange"]
      (is (= expected (ops/join-words data)))))
  (testing "join 3 words"
    (let [data ["apple" "orange" "pear"]
          expected "apple, orange and pear"]
      (is (= expected (ops/join-words data)))))
  (testing "join 4 words"
    (let [data ["apple" "orange" "pear" "mango"]
          expected "apple, orange, pear and mango"]
      (is (= expected (ops/join-words data))))))
