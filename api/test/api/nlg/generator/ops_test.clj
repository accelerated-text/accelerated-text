(ns api.nlg.generator.ops-test
  (:require [api.nlg.generator.ops :as ops]
            [clojure.test :refer [deftest is testing]]))

(deftest test-merge-contexts
  (testing "merge two simple contexts"
    (let [left {:static ["A"] :dynamic [] :reader-profile :default}
          right {:static ["B", "C"] :dynamic []}
          expected {:static ["A" "B" "C"] :dynamic [] :reader-profile :default}
          result (ops/merge-context left right)]
      (is (= expected result))))
  (testing "merge multiple contexts"
    (let [main {:static [] :dynamic [] :reader-profile :default}
          children [{:static ["A"]}
                    {:static ["B"]}
                    {:static ["C"]}]
          expected {:static ["A" "B" "C"] :dynamic [] :reader-profile :default}
          result (ops/merge-contexts main children)]
      (is (= expected result)))))

(deftest test-wordlist-filter
  (testing "Filter simple wordlist"
    (let [data (list {:name "w1", :attrs {:type :wordlist :class :test}}
                     {:name "w2", :attrs {:type :wordlist :class :test}}
                     {:name "w3", :attrs {:type :wordlist :class :test}})
          result (ops/distinct-wordlist data)]
      (is (= 1 (count result))))))

(deftest test-multi-replace
  (is (= "Sparta battled with Athens"
         (ops/replace-multi "{{AGENT}} battled with {{CO-AGENT}}" (list
                                                                    ["{{AGENT}}" "Sparta"]
                                                                    ["{{CO-AGENT}}" "Athens"])))))

(deftest join-sentences-test
  (let [s1 "Hello world"
        s2 "it's a very hot day"
        result (ops/join-sentences [s1 s2])]
    (is (= "Hello world. It's a very hot day." result))))
