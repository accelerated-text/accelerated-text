(ns nlg.api.data-test
  (:require [clojure.test :refer :all]
            [nlg.api.utils :refer :all]
            [nlg.api.data :refer :all]))

(deftest ^:integration reading-csv-from-s3
  (testing "Test listing files for user"
    (let [result (list-data {:limit 10 :user "example-user"})
          body (result :body)
          first-item (first body)]
      (is (= (:key first-item) "example-user/books.csv"))
      (is (= (:fieldNames first-item) [:id
                                       :isbn-13
                                       :title
                                       :subtitle
                                       :authors
                                       :publisher
                                       :publishedDate
                                       :pageCount
                                       :categories
                                       :averageRating
                                       :ratingsCount
                                       :maturityRating
                                       :thumbnail
                                       :language])))))
