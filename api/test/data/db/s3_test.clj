(ns data.db.s3-test
  (:require [api.test-utils :refer [with-dev-aws-credentials]]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.string :as str]
            [data.db.s3 :as s3]
            [data.db.config :as config]))

(use-fixtures :once with-dev-aws-credentials)

(deftest ^:integration reading-from-s3
  (testing "List files in bucket"
    (let [result (s3/list-objects config/data-bucket "example-user")]
      (is (pos? (count result)))))
  (testing "Read books.csv headers"
    (let [result (s3/read-file config/data-bucket "example-user/books.csv")
          headers (-> result (str/split-lines) (first) (str/split #",") (set))]
      (is (= #{"pageCount" "publishedDate" "ratingsCount" "authors" "maturityRating"
               "id" "categories" "averageRating" "thumbnail" "subtitle"
               "title" "publisher" "language" "isbn-13"} headers)))))
