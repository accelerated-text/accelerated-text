(ns lt.tokenmill.nlg.api.dictionary-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.api.dictionary :refer :all]))

(deftest test-filter-with-not
  (let [data (list {:text "test1" :flags {:default :YES :f1 :DONT_CARE :f2 :DONT_CARE}}
                   {:text "test2" :flags {:default :YES :f1 :YES :f2 :NO}}
                   {:text "test3" :flags {:default :YES :f1 :YES :f2 :DONT_CARE}}
                   {:text "test4" :flags {:default :NO :f1 :DONT_CARE :f2 :YES}})]
    (testing "Should filter out 'test2' because f2 = NO"
      (is (= #{"test1" "test3", "test4"} (set (filter-by-profile data {:f1 true :f2 true})))))
    (testing "Should return all values, because we don't give f2 profile"
      (is (= #{"test1" "test2" "test3"} (set (filter-by-profile data {:f1 true :f2 false})))))))

(deftest ^:integration query-database
  (let [result (search "see" {:junior true :senior true})]
    (is (= #{"see" "contemplate" "check out" "look" "watch"} (set result)))))
