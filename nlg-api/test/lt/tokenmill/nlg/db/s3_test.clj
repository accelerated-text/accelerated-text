(ns lt.tokenmill.nlg.db.s3-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.db.s3 :as s3]
            [lt.tokenmill.nlg.db.config :as config]
            [lt.tokenmill.nlg.api.utils :as utils]))


(deftest ^:integration reading-from-s3
  (testing "Read test.txt"
    (let [result (s3/read-file config/data-bucket "test.txt")]
      (is (= "testas\n" result))))
  (testing "Check ACL test.txt"
    (let [result (s3/get-acl config/data-bucket "test.txt")] 
      (println (format "Permissions for test.txt: %s", (list result)))))
  (testing "List files in bucket"
    (let [result (s3/list-files config/data-bucket "example-user" 20)]
      (println (format "Files in folder: %s" (list result)))
      (is (> (count result) 0))))
  (testing "Read example.csv"
    (let [result (s3/read-file config/data-bucket  "example-user/example.csv")]
      (is (= "first,second,third\none,two,three\n" result)))))
