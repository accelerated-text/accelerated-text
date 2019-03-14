(ns lt.tokenmill.nlg.db.s3-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.db.s3 :as s3]
            [lt.tokenmill.nlg.db.config :as config]
            [lt.tokenmill.nlg.api.utils :as utils]))

(deftest reading-from-s3
  (testing "Read test.txt"
    (let [result (s3/read-file config/data-bucket "test.txt")]
      (is (= "testas\n" result))))
  (testing "List files in bucket"
    (let [result (s3/list-files config/data-bucket "example-user")]
      (is (= (list {:key "example-user/example.csv"}) result)))))
