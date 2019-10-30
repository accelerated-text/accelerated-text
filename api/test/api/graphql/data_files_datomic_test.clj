(ns api.graphql.data-files-datomic-test
  (:require [api.db-fixtures :refer [clean-db]]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [data.entities.data-files :as data-files]
            [clojure.string :as str]))

(use-fixtures :each clean-db)

(deftest ^:integration writing-an-reading
  (testing "Read books.csv headers"
    (let [data-file-id (data-files/store!
                         {:filename "example-user/books.csv"
                          :content  (slurp "test/resources/accelerated-text-data-files/example-user/books.csv")})
          result (data-files/read-data-file-content "example-user" data-file-id)
          headers (-> result (str/split-lines) (first) (str/split #",") (set))]
      (is (= #{"pageCount" "publishedDate" "ratingsCount" "authors" "maturityRating"
               "id" "categories" "averageRating" "thumbnail" "subtitle"
               "title" "publisher" "language" "isbn-13"} headers)))))
