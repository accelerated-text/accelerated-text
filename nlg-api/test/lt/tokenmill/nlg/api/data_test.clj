(ns lt.tokenmill.nlg.api.data-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.api.utils :refer :all]
            [lt.tokenmill.nlg.api.data :refer :all]))

(deftest reading-csv-from-s3
  (testing "Test listing files for user"
    (let [result (list-data {:limit 10 :user "example-user"})
          body (result :body)
          first-item (first body)]
      (is (= (:key first-item) "example-user/example.csv"))
      (is (= (:fieldNames first-item) (list :first :second :third)))))
  (testing "Test get concrete csv"
    (let [result (read-data {:user "example-user" :file "example.csv"})
          body (result :body)]
      (is (= body {:data (list {:first "one"
                           :second "two"
                           :third "three"})
                   :key "example-user/example.csv"})))))
