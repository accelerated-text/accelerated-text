(ns lt.tokenmill.nlg.db.s3-test
  (:require [clojure.test :refer :all]
            [lt.tokenmill.nlg.db.s3 :as s3]
            [lt.tokenmill.nlg.db.config :as config]
            [lt.tokenmill.nlg.api.utils :as utils]))


(deftest ^:integration reading-from-s3
  (testing "List files in bucket"
    (let [result (s3/list-files config/data-bucket "example-user" 20)]
      (is (> (count result) 0))))
  (testing "Read data-example.csv"
    (let [result (s3/read-file config/data-bucket  "example-user/data-example.csv")]
      (is (= "\"Product name\",\"Main Feature\",\"Secondary feature\",\"Style\",\"Lacing\"\n\"Nike Air\",\"comfort\",\"support\",\"with sleek update on a classic design\",\"premium lacing\"" result)))))
