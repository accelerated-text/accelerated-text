(ns data.db.s3-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.string :as str]
            [data.db.s3 :as s3]
            [data.db.config :as config]))

(deftest ^:integration reading-from-s3
  (testing "List files in bucket"
    (let [result (s3/list-objects config/data-bucket "example-user")]
      (is (> (count result) 0))))
  (testing "Read books.csv headers"
    (let [result (s3/read-file config/data-bucket "example-user/books.csv")
          headers (-> result (str/split-lines) (first) (str/split #",") (set))]
      (is (= #{"pageCount" "publishedDate" "ratingsCount" "authors" "maturityRating"
               "id" "categories" "averageRating" "thumbnail" "subtitle"
               "title" "publisher" "language" "isbn-13"} headers)))))
