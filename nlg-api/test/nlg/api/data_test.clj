(ns nlg.api.data-test
  (:require [clojure.test :refer :all]
            [nlg.api.utils :refer :all]
            [nlg.api.data :refer :all]))

(deftest ^:integration reading-csv-from-s3
  (testing "Test listing files for user"
    (let [result (list-data {:limit 10 :user "example-user"})
          body (result :body)
          first-item (first body)]
      (is (= (:key first-item) "example-user/ccg-example.csv"))
      (is (= (:fieldNames first-item) (list
                                       (keyword "Product name")
                                       (keyword "Main Feature")
                                       (keyword "Secondary feature"))))))
  (testing "Test get concrete csv"
    (let [result (read-data {:user "example-user" :file "example.csv"})
          body (result :body)]
      (is (= body {:data (list {:first "one"
                           :second "two"
                           :third "three"})
                   :key "example-user/example.csv"})))))
